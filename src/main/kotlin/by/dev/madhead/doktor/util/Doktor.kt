package by.dev.madhead.doktor.util

import by.dev.madhead.doktor.Messages
import by.dev.madhead.doktor.model.DoktorConfig
import by.dev.madhead.doktor.model.RenderedDok
import by.dev.madhead.doktor.util.fs.WorkspaceDokLister
import by.dev.madhead.doktor.util.render.DokRenderer
import hudson.FilePath
import hudson.model.TaskListener
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers

fun diagnose(doktorConfig: DoktorConfig, workspace: FilePath, taskListener: TaskListener): Observable<RenderedDok> {
	return Observable
		.fromFuture(
			workspace.actAsync(WorkspaceDokLister(doktorConfig)),
			Schedulers.computation()
		)
		.flatMap { it.toObservable() }
		.map {
			Pair(it, it.filePath.actAsync(DokRenderer(it.markup, taskListener)))
		}
		.flatMap { (dok, renderedDokFuture) ->
			Observable
				.fromFuture(renderedDokFuture)
				.doOnError {
					taskListener.error(Messages.doktor_util_DoktorKt_diagnose_renderError(dok.filePath, it))
				}
				.onExceptionResumeNext(Observable.empty<RenderedDok>())
		}
		.doOnNext {
			taskListener.logger.println(it)
		}
}
