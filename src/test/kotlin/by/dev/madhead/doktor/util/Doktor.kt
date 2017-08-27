package by.dev.madhead.doktor.util

import by.dev.madhead.doktor.model.Markup
import by.dev.madhead.doktor.model.RenderedDok
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.TopologicalOrderIterator
import org.testng.annotations.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class Doktor {
	/**
	 * This test does not real [diagnose][by.dev.madhead.doktor.util.diagnose] function, but instead it tests its own, "mocked" flow.
	 * The reason is that `diagnose` uses Jenkins-specific classes and cannot be run outside of a Jenkins.
	 * So, keep this flow as much similar to the one in `diagnose` and use this test for quick experiments.
	 * Maybe one day it's better to refactor `diagnose` to be more testable, but that day has not come yet.
	 */
	@Test
	fun mockDataFlow() {
		val observer = Observable
			.fromFuture(
				CompletableFuture.supplyAsync {
					listOf(
						Pair(Markup.ASCIIDOC, "traits/habitat/domestic.asc".content),
						Pair(Markup.ASCIIDOC, "traits/habitat/natural.asciidoc".content),
						Pair(Markup.ASCIIDOC, "history/history.adoc".content),
						Pair(Markup.MARKDOWN, "traits/behavior.markdown".content),
						Pair(Markup.MARKDOWN, "INDEX.md".content),
						Pair(Markup.MARKDOWN, "traits/index.md".content)
					)
				}
			)
			.flatMap { it.toObservable() }
			.map { (markup, content) ->
				Pair(markup, CompletableFuture.supplyAsync { markup.render(content) })
			}
			.flatMap { (_, renderedDokFuture) ->
				Observable
					.fromFuture(renderedDokFuture)
					.doOnError {
						println("Error while rendering content: ${it}")
					}
					.onExceptionResumeNext(Observable.empty<RenderedDok>())

			}
			.collectInto(ConcurrentHashMap<String, RenderedDok>()) { map, renderedDok ->
				map[renderedDok.frontMatter.title] = renderedDok
			}
			.map { renderedDoksMap ->
				val graph = DefaultDirectedGraph<RenderedDok, DefaultEdge>(DefaultEdge::class.java)

				renderedDoksMap.values.forEach {
					graph.addVertex(it)
				}
				renderedDoksMap.values.forEach {
					if (!it.frontMatter.parent.isNullOrBlank()) {
						graph.addEdge(renderedDoksMap[it.frontMatter.parent!!], it)
					}
				}

				graph
			}
			.map {
				TopologicalOrderIterator(it)
			}
			.flatMapObservable { it.toObservable() }
			.test()

		observer.run {
			awaitTerminalEvent(60, TimeUnit.SECONDS)

			assertComplete()
			assertNoErrors()
			assertValueCount(6)

			this.assertValueAt(0) { it.frontMatter.title == "Cavia" }
			this.assertValueAt(1) { it.frontMatter.title == "Traits and environment" }
			this.assertValueAt(2) { it.frontMatter.title == "History" }
			this.assertValueAt(3) { it.frontMatter.title == "Domestic habitat" }
			this.assertValueAt(4) { it.frontMatter.title == "Natural habitat" }
			this.assertValueAt(5) { it.frontMatter.title == "Behavior" }
		}
	}

	private val String.content: String
		get() = this::class.java.getResourceAsStream("/cavia/${this}").bufferedReader().use { it.readText() }
}
