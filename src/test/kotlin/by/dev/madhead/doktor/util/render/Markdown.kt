package by.dev.madhead.doktor.util.render

import by.dev.madhead.doktor.model.RenderedContent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class Markdown {
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
			"labels"
		).map {
			arrayOf(
				this::class.java.getResourceAsStream("/by/dev/madhead/doktor/util/render/Markdown/${it}.md").bufferedReader().use { it.readText() },
				objectMapper.readValue<RenderedContent>(this::class.java.getResourceAsStream("/by/dev/madhead/doktor/util/render/Markdown/${it}.yml"))
			)
		}.toTypedArray()
	}

	@Test(dataProvider = "data")
	fun valid(input: String, output: RenderedContent) {
		Assert.assertEquals(markdown(input), output, "Unexpected output")
	}

	@Test(expectedExceptions = arrayOf(RenderException::class))
	fun `invalid - no front matter`() {
		markdown("# Content without front matter is not valid.")
	}

	@Test(expectedExceptions = arrayOf(RenderException::class))
	fun `invalid - empty front matter`() {
		markdown("---\n---\n# Content with empty front matter is not valid.")
	}

	@Test(expectedExceptions = arrayOf(RenderException::class))
	fun `invalid - no title`() {
		markdown("---\nparent: Parent\n---\n# Content without title is not valid.")
	}
}
