package by.dev.madhead.doktor.util.render

import by.dev.madhead.doktor.model.CONFLUENCE_PATH
import by.dev.madhead.doktor.model.FrontMatter
import by.dev.madhead.doktor.model.RenderedDok
import org.asciidoctor.Asciidoctor
import org.asciidoctor.OptionsBuilder
import org.jruby.RubyInstanceConfig
import org.jruby.javasupport.JavaEmbedUtils

fun asciiDoc(content: String): RenderedDok {
	// This crap is totally legal: https://github.com/asciidoctor/asciidoctorj#using-asciidoctorj-in-an-osgi-environment
	val config = RubyInstanceConfig()
	val classLoader = object : Any() {}::class.java.classLoader

	config.loader = classLoader
	JavaEmbedUtils.initialize(listOf("META-INF/jruby.home/lib/ruby/2.0", "gems/asciidoctor-1.5.4/lib"), config)

	val asciidoctor = Asciidoctor.Factory.create(classLoader)

	try {
		val header = asciidoctor.readDocumentHeader(content)

		return RenderedDok(
			asciidoctor.render(content, OptionsBuilder.options()),
			FrontMatter(
				header.attributes[CONFLUENCE_PATH].toString()
			)
		)
	} finally {
		asciidoctor.shutdown()
	}
}
