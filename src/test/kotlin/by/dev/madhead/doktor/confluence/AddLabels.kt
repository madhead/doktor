package by.dev.madhead.doktor.confluence

import by.dev.madhead.doktor.model.confluence.Label
import org.testng.Assert
import org.testng.annotations.Test
import java.util.concurrent.TimeUnit

class AddLabels : BaseConfluenceTest() {
	@Test
	fun `anonymous, success`() {
		addLabels(anonymousConfluence, "57005", listOf(Label(name = "test"), Label(name = "labels")))
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertValue {
					Assert.assertEquals(it.results, listOf(Label(name = "test"), Label(name = "labels")))
					Assert.assertEquals(it.start, 0)
					Assert.assertEquals(it.limit, 200)
					Assert.assertEquals(it.size, 2)

					true
				}
			}
	}

	@Test
	fun `blank, success`() {
		addLabels(blankConfluence, "57005", listOf(Label(name = "test"), Label(name = "labels")))
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertValue {
					Assert.assertEquals(it.results, listOf(Label(name = "test"), Label(name = "labels")))
					Assert.assertEquals(it.start, 0)
					Assert.assertEquals(it.limit, 200)
					Assert.assertEquals(it.size, 2)

					true
				}
			}
	}

	@Test
	fun `identified, success`() {
		addLabels(identifiedConfluence, "57006", listOf(Label(name = "test"), Label(name = "labels")))
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertValue {
					Assert.assertEquals(it.results, listOf(Label(name = "test"), Label(name = "labels")))
					Assert.assertEquals(it.start, 0)
					Assert.assertEquals(it.limit, 200)
					Assert.assertEquals(it.size, 2)

					true
				}
			}
	}

	@Test
	fun `unsecured, success`() {
		addLabels(unsecuredConfluence, "57010", listOf(Label(name = "test"), Label(name = "labels")))
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertValue {
					Assert.assertEquals(it.results, listOf(Label(name = "test"), Label(name = "labels")))
					Assert.assertEquals(it.start, 0)
					Assert.assertEquals(it.limit, 200)
					Assert.assertEquals(it.size, 2)

					true
				}
			}
	}

	@Test
	fun `anonymous, no content`() {
		addLabels(anonymousConfluence, "57007", listOf(Label(name = "test"), Label(name = "labels")))
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertError {
					it as ConfluenceException
					Assert.assertEquals(it.message, "No content found with id : ContentId{id=57007}")

					true
				}
			}
	}

	@Test
	fun `anonymous, 401`() {
		addLabels(anonymousConfluence, "57008", listOf(Label(name = "test"), Label(name = "labels")))
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertError {
					it as ConfluenceException
					Assert.assertEquals(it.message, "HTTP Exception 401 Unauthorized")

					true
				}
			}
	}

	@Test
	fun `anonymous, 500`() {
		addLabels(anonymousConfluence, "57009", listOf(Label(name = "test"), Label(name = "labels")))
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertError {
					it as ConfluenceException
					Assert.assertEquals(it.message, "HTTP Exception 500 Server Error")

					true
				}
			}
	}
}
