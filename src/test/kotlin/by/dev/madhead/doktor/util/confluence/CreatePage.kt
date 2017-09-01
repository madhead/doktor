package by.dev.madhead.doktor.util.confluence

import by.dev.madhead.doktor.model.confluence.*
import com.github.kittinunf.fuel.core.FuelError
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
					it as FuelError
					Assert.assertEquals(it.response.httpStatusCode, 400)

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
					it as FuelError
					Assert.assertEquals(it.response.httpStatusCode, 400)

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
					it as FuelError
					Assert.assertEquals(it.response.httpStatusCode, 401)

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
					it as FuelError
					Assert.assertEquals(it.response.httpStatusCode, 500)

					true
				}
			}
	}
}
