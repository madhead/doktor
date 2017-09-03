package by.dev.madhead.doktor.util.render

import by.dev.madhead.doktor.Messages
import by.dev.madhead.doktor.model.FRONTMATTER_PARENT
import by.dev.madhead.doktor.model.FRONTMATTER_TITLE
import by.dev.madhead.doktor.model.FrontMatter
import by.dev.madhead.doktor.model.Markup.MARKDOWN
import by.dev.madhead.doktor.model.RenderedContent
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.yaml.front.matter.AbstractYamlFrontMatterVisitor
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet

fun markdown(content: String): RenderedContent {
	val options = MutableDataSet().apply {
		set(Parser.EXTENSIONS, listOf(YamlFrontMatterExtension.create(), TablesExtension.create()))
	}
	val parser = Parser.builder(options).build()
	val htmlRenderer = HtmlRenderer.builder(options).build()
	val document = parser.parse(content)
	val visitor = AbstractYamlFrontMatterVisitor()

	visitor.visit(document)

	return RenderedContent(
		MARKDOWN,
		htmlRenderer.render(document),
		FrontMatter(
			visitor.data?.get(FRONTMATTER_TITLE)?.get(0) ?: throw RenderException(Messages.doktor_util_render_RenderException_titleRequired()),
			visitor.data?.get(FRONTMATTER_PARENT)?.get(0)
		)
	)
}
