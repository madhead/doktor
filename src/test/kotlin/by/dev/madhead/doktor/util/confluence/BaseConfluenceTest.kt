package by.dev.madhead.doktor.util.confluence

import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest

open class BaseConfluenceTest {
	lateinit private var wiremock: WireMockServer
	lateinit protected var anonymousConfluence: ResolvedConfluenceServer
	lateinit protected var identifiedConfluence: ResolvedConfluenceServer

	@BeforeTest
	fun startWireMock() {
		wiremock = WireMockServer(
			WireMockConfiguration.options()
				.dynamicPort()
				.usingFilesUnderClasspath("by/dev/madhead/doktor/util/confluence/${this::class.simpleName}")
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
}
