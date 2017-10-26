package by.dev.madhead.doktor.config

import hudson.util.FormValidation
import org.testng.Assert
import org.testng.annotations.Test

class ConfluenceServerDescriptorTest {
	@Test
	fun doCheckNameNull() {
		Assert.assertEquals(ConfluenceServer.ConfluenceServerDescriptor().doCheckName(null).kind, FormValidation.Kind.ERROR)
	}

	@Test
	fun doCheckNameBlank() {
		Assert.assertEquals(ConfluenceServer.ConfluenceServerDescriptor().doCheckName("").kind, FormValidation.Kind.ERROR)
	}

	@Test
	fun doCheckName() {
		Assert.assertEquals(ConfluenceServer.ConfluenceServerDescriptor().doCheckName("TEST").kind, FormValidation.Kind.OK)
	}

	@Test
	fun doCheckUrlNull() {
		Assert.assertEquals(ConfluenceServer.ConfluenceServerDescriptor().doCheckUrl(null).kind, FormValidation.Kind.ERROR)
	}

	@Test
	fun doCheckUrlBlank() {
		Assert.assertEquals(ConfluenceServer.ConfluenceServerDescriptor().doCheckUrl("").kind, FormValidation.Kind.ERROR)
	}

	@Test
	fun doCheckUrl() {
		Assert.assertEquals(ConfluenceServer.ConfluenceServerDescriptor().doCheckUrl("http://confluence.example.com").kind, FormValidation.Kind.OK)
	}

	@Test
	fun doCheckSpaceNull() {
		Assert.assertEquals(ConfluenceServer.ConfluenceServerDescriptor().doCheckSpace(null).kind, FormValidation.Kind.ERROR)
	}

	@Test
	fun doCheckSpaceBlank() {
		Assert.assertEquals(ConfluenceServer.ConfluenceServerDescriptor().doCheckSpace("").kind, FormValidation.Kind.ERROR)
	}

	@Test
	fun doCheckSpace() {
		Assert.assertEquals(ConfluenceServer.ConfluenceServerDescriptor().doCheckSpace("TEST").kind, FormValidation.Kind.OK)
	}
}
