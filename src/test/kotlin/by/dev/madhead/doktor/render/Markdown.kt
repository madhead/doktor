package by.dev.madhead.doktor.render

import by.dev.madhead.doktor.model.RenderedContent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.io.File

class Markdown {
	@DataProvider(name = "valids")
	fun valids(): Array<Array<*>> {
		val objectMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

		return listOf(
			"simple",
			"steeper",
			"luke",
			"table",
			"img",
			"single_label",
			"single_label_inline",
			"labels"
		).map {
			arrayOf(
				File(this::class.java.getResource("/by/dev/madhead/doktor/render/Markdown/valids/${it}.md").toURI()),
				objectMapper.readValue<RenderedContent>(this::class.java.getResourceAsStream("/by/dev/madhead/doktor/render/Markdown/valids/${it}.yml"))
			)
		}.toTypedArray()
	}

	@DataProvider(name = "invalids")
	fun invalids(): Array<Array<*>> {

		return listOf(
			"no_front_matter",
			"empty_front_matter",
			"invalid_front_matter",
			"no_title"
		).map {
			arrayOf(
				File(this::class.java.getResource("/by/dev/madhead/doktor/render/Markdown/invalids/${it}.md").toURI())
			)
		}.toTypedArray()
	}

	@Test(dataProvider = "valids")
	fun valid(input: File, output: RenderedContent) {
		Assert.assertEquals(markdown(input), output, "Unexpected output")
	}

	@Test(dataProvider = "invalids", expectedExceptions = arrayOf(RenderException::class))
	fun invalid(input: File) {
		markdown(input)
	}
}
