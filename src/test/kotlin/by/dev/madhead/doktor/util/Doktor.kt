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

@Test(enabled = false)
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
						Pair(Markup.ASCIIDOC, "traits/habitat/domestic.asc"),
						Pair(Markup.ASCIIDOC, "traits/habitat/natural.asciidoc"),
						Pair(Markup.ASCIIDOC, "history/history.adoc"),
						Pair(Markup.MARKDOWN, "traits/behavior.markdown"),
						Pair(Markup.MARKDOWN, "INDEX.md"),
						Pair(Markup.MARKDOWN, "traits/index.md")
					).map {
						Triple(it.first, it.second, it.second.content)
					}
				}
			)
			.flatMap { it.toObservable() }
			.map { (markup, originalPath, content) ->
				Pair(originalPath, CompletableFuture.supplyAsync { RenderedDok(originalPath, markup.render(content)) })
			}
			.flatMap { (originalPath, renderedDokFuture) ->
				Observable
					.fromFuture(renderedDokFuture)
					.doOnError {
						println("Error while rendering file ${originalPath}: ${it}")
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
						graph.addEdge(renderedDoksMap[it.content.frontMatter.parent!!], it)
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

			this.assertValueAt(0) { it.content.frontMatter.title == "Cavia" }
			this.assertValueAt(1) { it.content.frontMatter.title == "Traits and environment" }
			this.assertValueAt(2) { it.content.frontMatter.title == "History" }
			this.assertValueAt(3) { it.content.frontMatter.title == "Domestic habitat" }
			this.assertValueAt(4) { it.content.frontMatter.title == "Natural habitat" }
			this.assertValueAt(5) { it.content.frontMatter.title == "Behavior" }
		}
	}

	private val String.content: String
		get() = this::class.java.getResourceAsStream("/cavia/${this}").bufferedReader().use { it.readText() }
}
