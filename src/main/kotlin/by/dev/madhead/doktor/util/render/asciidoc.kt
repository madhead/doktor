package by.dev.madhead.doktor.util.render

import by.dev.madhead.doktor.model.CONFLUENCE_PATH
import by.dev.madhead.doktor.model.FrontMatter
import by.dev.madhead.doktor.model.RenderedDok
import com.vladsch.flexmark.ext.yaml.front.matter.AbstractYamlFrontMatterVisitor
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet

fun asciiDoc(content: String): RenderedDok {
	val options = MutableDataSet().apply {
		set(Parser.EXTENSIONS, listOf(YamlFrontMatterExtension.create()))
	}
	val parser = Parser.builder(options).build()
	val htmlRenderer = HtmlRenderer.builder(options).build()
	val document = parser.parse(content)
	val visitor = AbstractYamlFrontMatterVisitor()

	visitor.visit(document)

	return RenderedDok(
		htmlRenderer.render(document),
		FrontMatter(
			visitor.data?.get(CONFLUENCE_PATH)?.get(0)
		)
	)
}
