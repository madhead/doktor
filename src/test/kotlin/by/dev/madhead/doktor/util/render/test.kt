package by.dev.madhead.doktor.util.render

import org.asciidoctor.Asciidoctor
import org.asciidoctor.AttributesBuilder
import org.asciidoctor.OptionsBuilder
import org.jruby.RubyInstanceConfig
import org.jruby.javasupport.JavaEmbedUtils
import org.testng.annotations.Test

class test {
	@Test
	fun `front matter`() {
		// This crap is totally legal: https://github.com/asciidoctor/asciidoctorj#using-asciidoctorj-in-an-osgi-environment
		val config = RubyInstanceConfig()
		val classLoader = object : Any() {}::class.java.classLoader

		config.loader = classLoader
		JavaEmbedUtils.initialize(listOf("META-INF/jruby.home/lib/ruby/2.0", "gems/asciidoctor-1.5.4/lib"), config)

		val asciidoctor = Asciidoctor.Factory.create(classLoader)

		val content =
			"""---
title: Test
tags:
  - tags
  - are
  - cool
---

## Title

test paragraph

http://google.com[link]
"""

		try {
			val options = OptionsBuilder.options().backend("xhtml").attributes(AttributesBuilder.attributes().skipFrontMatter(true))
			val doc = asciidoctor.readDocumentStructure(content, options.asMap())
			val header = asciidoctor.readDocumentHeader(content)

			doc.toString()

			println(doc.header.attributes["front-matter"])
			println(header.attributes["front-matter"])
		} finally {
			asciidoctor.shutdown()
		}
	}
}
