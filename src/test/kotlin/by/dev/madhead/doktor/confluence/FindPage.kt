package by.dev.madhead.doktor.confluence

import org.testng.Assert
import org.testng.annotations.Test
import java.util.concurrent.TimeUnit

class FindPage : BaseConfluenceTest() {
	@Test
	fun `anonymous, no pages, success`() {
		findPage(anonymousConfluence, "anonymous, no pages, success")
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertResult()
			}
	}

	@Test
	fun `anonymous, one page, success`() {
		findPage(anonymousConfluence, "anonymous, one page, success")
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertValue {
					Assert.assertEquals(it.id, "57005")

					true
				}
			}
	}

	@Test
	fun `anonymous, 404`() {
		findPage(anonymousConfluence, "anonymous, 404")
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertError {
					it as ConfluenceException
					Assert.assertEquals(it.message, "No space with key : TEST")

					true
				}
			}
	}

	@Test
	fun `anonymous, 500`() {
		findPage(anonymousConfluence, "anonymous, 500")
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

	@Test
	fun `identified, no pages, success`() {
		findPage(identifiedConfluence, "identified, no pages, success")
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertResult()
			}
	}

	@Test
	fun `identified, no pages, 401`() {
		findPage(anonymousConfluence, "identified, no pages, success")
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
}
