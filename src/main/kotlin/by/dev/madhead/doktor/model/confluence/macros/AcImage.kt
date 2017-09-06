package by.dev.madhead.doktor.model.confluence.macros

import kotlinx.html.HTMLTag
import kotlinx.html.HtmlBlockTag
import kotlinx.html.TagConsumer
import kotlinx.html.visitAndFinalize

class AC_IMAGE(consumer: TagConsumer<*>) :
	HTMLTag(
		tagName = "ac:image",
		consumer = consumer,
		initialAttributes = emptyMap(),
		inlineTag = false,
		emptyTag = false
	), HtmlBlockTag {
	var acHeight: String
		get() = attributes["ac:height"]!!
		set(value) {
			attributes["ac:height"] = value
		}
	var acWidth: String
		get() = attributes["ac:width"]!!
		set(value) {
			attributes["ac:width"] = value
		}
	var acAlt: String
		get() = attributes["ac:alt"]!!
		set(value) {
			attributes["ac:alt"] = value
		}
	var acTitle: String
		get() = attributes["ac:title"]!!
		set(value) {
			attributes["ac:title"] = value
		}
}

fun AC_IMAGE.riAttachment(fileName: String) {
	RI_ATTACHMENT(consumer).visitAndFinalize(consumer) {
		riFileName = fileName
	}
}

fun AC_IMAGE.riUrl(url: String) {
	RI_URL(consumer, url).visitAndFinalize(consumer) {}
}

fun <T> TagConsumer<T>.acImage(block: AC_IMAGE.() -> Unit = {}): T {
	return AC_IMAGE(this).visitAndFinalize(this, block)
}
