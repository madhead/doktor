package by.dev.madhead.doktor.config

import org.testng.annotations.Test

class ConfluenceServerTest {
	@Test
	fun initNullCredentials() {
		ConfluenceServer(
			"TEST",
			"http://confluence.example.com",
			"TEST",
			null
		)
	}

	@Test
	fun init() {
		ConfluenceServer(
			"TEST",
			"http://confluence.example.com",
			"TEST",
			"TEST"
		)
	}
}
