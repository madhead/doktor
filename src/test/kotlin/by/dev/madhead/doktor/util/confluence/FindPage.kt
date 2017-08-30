package by.dev.madhead.doktor.util.confluence

import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import com.github.kittinunf.fuel.core.FuelError
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.testng.Assert
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import java.util.concurrent.TimeUnit


class FindPage {
	lateinit var wiremock: WireMockServer
	lateinit var anonymousConfluence: ResolvedConfluenceServer
	lateinit var identifiedConfluence: ResolvedConfluenceServer

	@BeforeTest
	fun startWireMock() {
		wiremock = WireMockServer(
			WireMockConfiguration
				.options()
				.dynamicPort()
				.usingFilesUnderClasspath("by/dev/madhead/doktor/util/confluence/FindPage")
		)
		wiremock.start()
		anonymousConfluence = ResolvedConfluenceServer(
			"TEST",
			"http://localhost:${wiremock.port()}",
			"TEST",
			null,
			null
		)
		identifiedConfluence = ResolvedConfluenceServer(
			"TEST",
			"http://localhost:${wiremock.port()}",
			"TEST",
			"test",
			"test"
		)
	}

	@AfterTest
	fun shutdownWireMock() {
		wiremock.shutdown()
	}

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
					it as FuelError
					Assert.assertEquals(it.response.httpStatusCode, 404)

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
					it as FuelError
					Assert.assertEquals(it.response.httpStatusCode, 500)

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
					it as FuelError
					Assert.assertEquals(it.response.httpStatusCode, 401)

					true
				}
			}
	}
}
