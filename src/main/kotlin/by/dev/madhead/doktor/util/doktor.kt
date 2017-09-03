package by.dev.madhead.doktor.util

import by.dev.madhead.doktor.Messages
import by.dev.madhead.doktor.model.DoktorConfig
import by.dev.madhead.doktor.model.RenderedDok
import by.dev.madhead.doktor.model.confluence.CreatePageResponse
import by.dev.madhead.doktor.util.confluence.upload
import by.dev.madhead.doktor.util.fs.WorkspaceDokLister
import by.dev.madhead.doktor.util.render.DokRenderer
import hudson.FilePath
import hudson.model.TaskListener
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.TopologicalOrderIterator
import java.util.concurrent.ConcurrentHashMap

fun diagnose(doktorConfig: DoktorConfig, workspace: FilePath, taskListener: TaskListener): Observable<CreatePageResponse> {
	return Observable
		.fromFuture(
			workspace.actAsync(WorkspaceDokLister(doktorConfig))
		)
		.flatMap { it.toObservable() }
		.map {
			Pair(it.filePath.remote, it.filePath.actAsync(DokRenderer(it.markup, taskListener)))
		}
		.flatMap { (originalPath, renderedDokFuture) ->
			Observable
				.fromFuture(renderedDokFuture)
				.doOnError {
					taskListener.error(Messages.doktor_util_diagnose_renderError(originalPath, it))
				}
				.onExceptionResumeNext(Observable.empty<RenderedDok>())
		}
		.collectInto(ConcurrentHashMap<String, RenderedDok>()) { map, renderedDok ->
			map[renderedDok.content.frontMatter.title] = renderedDok
		}
		.map { renderedDoksMap ->
			val graph = DefaultDirectedGraph<RenderedDok, DefaultEdge>(DefaultEdge::class.java)

			renderedDoksMap.values.forEach {
				graph.addVertex(it)
			}
			renderedDoksMap.values.forEach {
				if (!it.content.frontMatter.parent.isNullOrBlank()) {
					if (null != renderedDoksMap[it.content.frontMatter.parent!!]) {
						graph.addEdge(renderedDoksMap[it.content.frontMatter.parent!!], it)
					}
				}
			}

			graph
		}
		.map {
			TopologicalOrderIterator(it)
		}
		.flatMapObservable { it.toObservable() }
		.flatMapMaybe {
			upload(
				doktorConfig.server,
				it,
				taskListener
			)
		}
}
