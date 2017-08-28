package by.dev.madhead.doktor.util.render

import by.dev.madhead.doktor.model.FRONTMATTER_PARENT
import by.dev.madhead.doktor.model.FRONTMATTER_TITLE
import by.dev.madhead.doktor.model.FrontMatter
import by.dev.madhead.doktor.model.Markup.ASCIIDOC
import by.dev.madhead.doktor.model.RenderedContent
import org.asciidoctor.Asciidoctor
import org.asciidoctor.OptionsBuilder
import org.jruby.RubyInstanceConfig
import org.jruby.javasupport.JavaEmbedUtils

fun asciiDoc(content: String): RenderedContent {
	// This crap is totally legal: https://github.com/asciidoctor/asciidoctorj#using-asciidoctorj-in-an-osgi-environment
	val config = RubyInstanceConfig()
	val classLoader = object : Any() {}::class.java.classLoader

	config.loader = classLoader
	JavaEmbedUtils.initialize(listOf("META-INF/jruby.home/lib/ruby/2.0", "gems/asciidoctor-1.5.4/lib"), config)

	val asciidoctor = Asciidoctor.Factory.create(classLoader)

	try {
		val header = asciidoctor.readDocumentHeader(content)

		return RenderedContent(
			ASCIIDOC,
			asciidoctor.render(content, OptionsBuilder.options()),
			FrontMatter(
				header.attributes[FRONTMATTER_TITLE]?.toString() ?: throw RenderException("'title' is required in front matter"),
				header.attributes[FRONTMATTER_PARENT]?.toString()
			)
		)
	} finally {
		asciidoctor.shutdown()
	}
}
