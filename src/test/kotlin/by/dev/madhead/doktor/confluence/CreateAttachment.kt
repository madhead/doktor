package by.dev.madhead.doktor.confluence

import by.dev.madhead.doktor.model.Attachment
import org.testng.Assert
import org.testng.annotations.Test
import java.util.concurrent.TimeUnit

class CreateAttachment : BaseConfluenceTest() {
	@Test
	fun `anonymous, success`() {
		createAttachment(anonymousConfluence, "57005", Attachment("file.png", "bytes".toByteArray()))
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertValue {
					Assert.assertEquals(it.results.size, 1)
					Assert.assertEquals(it.results[0].id, "att57005")

					true
				}
			}
		createAttachment(blankConfluence, "57005", Attachment("file.png", "bytes".toByteArray()))
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertValue {
					Assert.assertEquals(it.results.size, 1)
					Assert.assertEquals(it.results[0].id, "att57005")

					true
				}
			}
	}

	@Test
	fun `identified, success`() {
		createAttachment(identifiedConfluence, "57006", Attachment("file.png", "bytes".toByteArray()))
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertValue {
					Assert.assertEquals(it.results.size, 1)
					Assert.assertEquals(it.results[0].id, "att57006")

					true
				}
			}
	}

	@Test
	fun `anonymous, 400`() {
		createAttachment(anonymousConfluence, "57007", Attachment("file.png", "bytes".toByteArray()))
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertError {
					it as ConfluenceException
					Assert.assertEquals(it.message, "The request was rejected because no multipart boundary was found")

					true
				}
			}
	}

	@Test
	fun `anonymous, 403`() {
		createAttachment(anonymousConfluence, "57008", Attachment("file.png", "bytes".toByteArray()))
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
		createAttachment(anonymousConfluence, "57009", Attachment("file.png", "bytes".toByteArray()))
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
