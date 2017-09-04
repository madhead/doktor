package by.dev.madhead.doktor.confluence

import org.testng.Assert
import org.testng.annotations.Test
import java.util.concurrent.TimeUnit

class DeletePage : BaseConfluenceTest() {
	@Test
	fun `anonymous, success`() {
		deletePage(anonymousConfluence, "57003")
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertValue(true)
			}
	}

	@Test
	fun `identified, success`() {
		deletePage(identifiedConfluence, "57004")
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertValue(true)
			}
	}

	@Test
	fun `anonymous, no content`() {
		deletePage(anonymousConfluence, "57005")
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertError {
					it as ConfluenceException
					Assert.assertEquals(it.message, "Cannot delete content with id 'ContentId{id=57005}': not found")

					true
				}
			}
	}

	@Test
	fun `anonymous, 403`() {
		deletePage(anonymousConfluence, "57006")
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertError {
					it as ConfluenceException
					Assert.assertEquals(it.message, "HTTP Exception 403 Forbidden")

					true
				}
			}
	}

	@Test
	fun `anonymous, 500`() {
		deletePage(anonymousConfluence, "57007")
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
