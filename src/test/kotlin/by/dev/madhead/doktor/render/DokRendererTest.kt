package by.dev.madhead.doktor.render

import by.dev.madhead.doktor.model.Markup
import by.dev.madhead.doktor.model.RenderedDok
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.tomakehurst.wiremock.WireMockServer
import hudson.model.TaskListener
import org.mockito.Mockito
import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.io.File
import java.io.PrintStream

class DokRendererTest {
	val objectMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
	lateinit private var wiremock: WireMockServer

	@DataProvider(name = "markdowns")
	fun markdowns(): Array<Array<*>> {
		return listOf(
			"local_image",
			"remote_image",
			"zero_image"
		).map {
			arrayOf(
				File(this::class.java.getResource("/by/dev/madhead/doktor/render/DokRenderer/md/${it}.md").toURI()),
				objectMapper.readValue<RenderedDok>(this::class.java.getResourceAsStream("/by/dev/madhead/doktor/render/DokRenderer/md/${it}.yml"))
			)
		}.toTypedArray()
	}

	@DataProvider(name = "asciidocs")
	fun asciidocs(): Array<Array<*>> {
		return listOf(
			"local_image_alt",
			"local_image_height",
			"local_image_no_alt",
			"local_image_title",
			"local_image_width",
			"remote_image_alt",
			"remote_image_height",
			"remote_image_no_alt",
			"remote_image_title",
			"remote_image_width",
			"zero_image"
		).map {
			arrayOf(
				File(this::class.java.getResource("/by/dev/madhead/doktor/render/DokRenderer/adoc/${it}.adoc").toURI()),
				objectMapper.readValue<RenderedDok>(this::class.java.getResourceAsStream("/by/dev/madhead/doktor/render/DokRenderer/adoc/${it}.yml"))
			)
		}.toTypedArray()
	}

	@Test(dataProvider = "markdowns")
	fun markdown(input: File, expected: RenderedDok) {
		test(Markup.MARKDOWN, input, expected)
	}

	@Test(dataProvider = "asciidocs")
	fun asciidoc(input: File, expected: RenderedDok) {
		test(Markup.ASCIIDOC, input, expected)
	}

	private fun test(markup: Markup, input: File, expected: RenderedDok) {
		val taskListener = Mockito.mock(TaskListener::class.java)
		val logger = Mockito.mock(PrintStream::class.java)
		val renderer = DokRenderer(markup, taskListener)

		Mockito.`when`(taskListener.logger).thenReturn(logger)

		val renderedDok = renderer.invoke(input, null)

		Assert.assertEquals(renderedDok.content, expected.content)
		Assert.assertEquals(renderedDok.images, expected.images)
	}
}
