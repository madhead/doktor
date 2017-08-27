package by.dev.madhead.doktor.util

import by.dev.madhead.doktor.model.Markup
import by.dev.madhead.doktor.model.RenderedDok
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import org.testng.annotations.Test
import java.util.concurrent.CompletableFuture
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
		val observable = Observable
			.fromFuture(
				CompletableFuture.supplyAsync {
					listOf(
						Pair(Markup.MARKDOWN, "INDEX.md".content),
						Pair(Markup.MARKDOWN, "traits/index.md".content),
						Pair(Markup.MARKDOWN, "traits/behavior.markdown".content),
						Pair(Markup.ASCIIDOC, "traits/habitat/natural.asciidoc".content),
						Pair(Markup.ASCIIDOC, "traits/habitat/domestic.asc".content),
						Pair(Markup.ASCIIDOC, "history/history.adoc".content)
					)
				},
				Schedulers.computation()
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
		val observer = TestObserver<RenderedDok>()

		observable.subscribe(observer)

		observer.run {
			awaitTerminalEvent(10, TimeUnit.SECONDS)

			assertComplete()
			assertNoErrors()
			assertValueCount(6)
		}
	}

	private val String.content: String
		get() = this::class.java.getResourceAsStream("/cavia/${this}").bufferedReader().use { it.readText() }
}
