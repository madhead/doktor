package by.dev.madhead.doktor.confluence

import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass

open class BaseConfluenceTest {
	lateinit private var wiremock: WireMockServer
	lateinit protected var anonymousConfluence: ResolvedConfluenceServer
	lateinit protected var blankConfluence: ResolvedConfluenceServer
	lateinit protected var identifiedConfluence: ResolvedConfluenceServer
	lateinit protected var unsecuredConfluence: ResolvedConfluenceServer

	@BeforeClass
	fun startWireMock() {
		wiremock = WireMockServer(
			WireMockConfiguration.options()
				.dynamicPort()
				.usingFilesUnderClasspath("by/dev/madhead/doktor/confluence/${this::class.simpleName}")
		)
		wiremock.start()
		anonymousConfluence = ResolvedConfluenceServer(
			"TEST",
			"http://localhost:${wiremock.port()}",
			"TEST",
			null,
			null
		)
		blankConfluence = ResolvedConfluenceServer(
			"TEST",
			"http://localhost:${wiremock.port()}",
			"TEST",
			"",
			""
		)
		identifiedConfluence = ResolvedConfluenceServer(
			"TEST",
			"http://localhost:${wiremock.port()}",
			"TEST",
			"test",
			"test"
		)
		unsecuredConfluence = ResolvedConfluenceServer(
			"TEST",
			"http://localhost:${wiremock.port()}",
			"TEST",
			"test",
			null
		)
	}

	@AfterClass
	fun shutdownWireMock() {
		wiremock.shutdown()
	}
}
