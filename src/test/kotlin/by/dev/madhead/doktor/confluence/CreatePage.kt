package by.dev.madhead.doktor.confluence

import by.dev.madhead.doktor.model.confluence.*
import org.testng.Assert
import org.testng.annotations.Test
import java.util.concurrent.TimeUnit

class CreatePage : BaseConfluenceTest() {
	@Test
	fun `anonymous, top level, success`() {
		createPage(anonymousConfluence, CreatePageRequest(
			title = "anonymous, top level, success",
			space = SpaceReference("TEST"),
			body = Body(Storage("<h1>Test</h1>"))
		))
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
	fun `identified, top level, success`() {
		createPage(identifiedConfluence, CreatePageRequest(
			title = "identified, top level, success",
			space = SpaceReference("TEST"),
			body = Body(Storage("<h1>Test</h1>"))
		))
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
	fun `anonymous, child, success`() {
		createPage(anonymousConfluence, CreatePageRequest(
			title = "anonymous, child, success",
			ancestors = listOf(ContentReference("57004")),
			space = SpaceReference("TEST"),
			body = Body(Storage("<h1>Test</h1>"))
		))
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
	fun `anonymous, child, no ancestor`() {
		createPage(anonymousConfluence, CreatePageRequest(
			title = "anonymous, child, no ancestor",
			ancestors = listOf(ContentReference("57004")),
			space = SpaceReference("TEST"),
			body = Body(Storage("<h1>Test</h1>"))
		))
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertError {
					it as ConfluenceException
					Assert.assertEquals(it.message, "The parent ID specified (ContentId{id=57004}) does not exist?")

					true
				}
			}
	}

	@Test
	fun `anonymous, top level, exist`() {
		createPage(anonymousConfluence, CreatePageRequest(
			title = "anonymous, top level, exist",
			space = SpaceReference("TEST"),
			body = Body(Storage("<h1>Test</h1>"))
		))
			.test()
			.run {
				awaitTerminalEvent(10, TimeUnit.SECONDS)

				assertError {
					it as ConfluenceException
					Assert.assertEquals(it.message, "A page with this title already exists: A page already exists with the title anonymous, top level, exist in the space with key TEST")

					true
				}
			}
	}

	@Test
	fun `anonymous, top level, 401`() {
		createPage(anonymousConfluence, CreatePageRequest(
			title = "anonymous, top level, 401",
			space = SpaceReference("TEST"),
			body = Body(Storage("<h1>Test</h1>"))
		))
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
	fun `anonymous, top level, 500`() {
		createPage(anonymousConfluence, CreatePageRequest(
			title = "anonymous, top level, 500",
			space = SpaceReference("TEST"),
			body = Body(Storage("<h1>Test</h1>"))
		))
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
