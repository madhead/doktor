package by.dev.madhead.doktor.util.render

import by.dev.madhead.doktor.model.RenderedContent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class AsciiDoc {
	@DataProvider(name = "data")
	fun data(): Array<Array<*>> {
		val objectMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

		return listOf(
			"simple",
			"steeper",
			"luke",
			"table",
			"img",
			"single_label",
			"single_label_inline",
			"labels",
			"labels_inline"
		).map {
			arrayOf(
				this::class.java.getResourceAsStream("/by/dev/madhead/doktor/util/render/AsciiDoc/${it}.asc").bufferedReader().use { it.readText() },
				objectMapper.readValue<RenderedContent>(this::class.java.getResourceAsStream("/by/dev/madhead/doktor/util/render/AsciiDoc/${it}.yml"))
			)
		}.toTypedArray()
	}

	@Test(dataProvider = "data")
	fun valid(input: String, output: RenderedContent) {
		Assert.assertEquals(asciiDoc(input), output, "Unexpected output")
	}

	@Test(expectedExceptions = arrayOf(RenderException::class))
	fun `invalid - no front matter`() {
		asciiDoc("# Content without front matter is not valid.")
	}

	@Test(expectedExceptions = arrayOf(RenderException::class))
	fun `invalid - empty front matter`() {
		asciiDoc("---\n---\n# Content with empty front matter is not valid.")
	}

	@Test(expectedExceptions = arrayOf(RenderException::class))
	fun `invalid - no title`() {
		asciiDoc("---\nparent: Parent\n---\n# Content without title is not valid.")
	}

	@Test(expectedExceptions = arrayOf(RenderException::class))
	fun `invalid - ivalid YAML`() {
		asciiDoc("---\n" +
			"title: Steeper: Truth or Fiction\n" +
			"---\n" +
			"\n" +
			"== AsciiDoc file with links\n" +
			"\n" +
			"[Links](https://google.com) are great!\n")
	}
}
