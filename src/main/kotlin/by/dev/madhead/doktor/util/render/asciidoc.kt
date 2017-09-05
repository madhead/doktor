package by.dev.madhead.doktor.util.render

import by.dev.madhead.doktor.Messages
import by.dev.madhead.doktor.model.*
import by.dev.madhead.doktor.model.Markup.ASCIIDOC
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.asciidoctor.Asciidoctor
import org.asciidoctor.AttributesBuilder
import org.asciidoctor.OptionsBuilder
import org.jruby.RubyInstanceConfig
import org.jruby.javasupport.JavaEmbedUtils

fun asciiDoc(content: String): RenderedContent {
	// This crap is totally legal: https://github.com/asciidoctor/asciidoctorj#using-asciidoctorj-in-an-osgi-environment
	val config = RubyInstanceConfig()
	val classLoader = object : Any() {}::class.java.classLoader

	config.loader = classLoader
	JavaEmbedUtils.initialize(listOf("META-INF/jruby.home/lib/ruby/2.0", "gems/asciidoctor-1.5.6.1/lib"), config)

	val asciidoctor = Asciidoctor.Factory.create(classLoader)

	try {
		val options = OptionsBuilder
			.options()
			.backend("xhtml")
			.attributes(AttributesBuilder
				.attributes()
				.skipFrontMatter(true)
			)
		val documentStructure = asciidoctor.readDocumentStructure(content, options.asMap())
		val content = asciidoctor.convert(content, options)
		val objectMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

		if (null == documentStructure.header.attributes["front-matter"]) {
			throw RenderException(Messages.doktor_util_render_RenderException_frontMatterRequired())
		}

		val frontMatter =
			try {
				objectMapper.readTree(documentStructure.header.attributes["front-matter"] as String) ?: throw RenderException(Messages.doktor_util_render_RenderException_frontMatterRequired())
			} catch (e: Throwable) {
				throw RenderException(Messages.doktor_util_render_RenderException_frontMatterInvalid())
			}

		return RenderedContent(
			ASCIIDOC,
			content,
			FrontMatter(
				title = frontMatter[FRONTMATTER_TITLE]?.asText() ?: throw RenderException(Messages.doktor_util_render_RenderException_titleRequired()),
				parent = frontMatter[FRONTMATTER_PARENT]?.asText(),
				labels = when (frontMatter[FRONTMATTER_LABELS]) {
					is TextNode -> listOf(frontMatter[FRONTMATTER_LABELS].asText())
					is ArrayNode -> frontMatter[FRONTMATTER_LABELS].map { it.asText() }
					else -> emptyList()
				}
			)
		)
	} finally {
		asciidoctor.shutdown()
	}
}
