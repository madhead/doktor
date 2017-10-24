package by.dev.madhead.doktor.confluence

import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass

open class BaseConfluenceTest {
	private lateinit var wiremock: WireMockServer
	protected lateinit var anonymousConfluence: ResolvedConfluenceServer
	protected lateinit var identifiedConfluence: ResolvedConfluenceServer

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
		identifiedConfluence = ResolvedConfluenceServer(
			"TEST",
			"http://localhost:${wiremock.port()}",
			"TEST",
			"test",
			"test"
		)
	}

	@AfterClass
	fun shutdownWireMock() {
		wiremock.shutdown()
	}
}
