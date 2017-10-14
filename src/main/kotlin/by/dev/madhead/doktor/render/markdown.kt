package by.dev.madhead.doktor.render

import by.dev.madhead.doktor.Messages
import by.dev.madhead.doktor.model.*
import by.dev.madhead.doktor.model.Markup.MARKDOWN
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.yaml.front.matter.AbstractYamlFrontMatterVisitor
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet
import java.io.File

fun markdown(file: File): RenderedContent {
	val options = MutableDataSet().apply {
		set(Parser.EXTENSIONS, listOf(YamlFrontMatterExtension.create(), TablesExtension.create()))
	}
	val parser = Parser.builder(options).build()
	val htmlRenderer = HtmlRenderer.builder(options).build()
	val document = parser.parse(file.readText())
	val visitor = AbstractYamlFrontMatterVisitor()

	visitor.visit(document)
	if ((null == visitor.data) || (visitor.data.isEmpty())) {
		throw RenderException(Messages.doktor_render_RenderException_frontMatterRequired())
	}

	return RenderedContent(
		MARKDOWN,
		htmlRenderer.render(document),
		FrontMatter(
			title = visitor.data[FRONTMATTER_TITLE]?.get(0) ?: throw RenderException(Messages.doktor_render_RenderException_titleRequired()),
			parent = visitor.data[FRONTMATTER_PARENT]?.get(0),
			labels = visitor.data[FRONTMATTER_LABELS] ?: emptyList()
		)
	)
}
