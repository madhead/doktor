package by.dev.madhead.doktor.model.confluence.macros

import kotlinx.html.HTMLTag
import kotlinx.html.HtmlBlockTag
import kotlinx.html.TagConsumer

class RI_ATTACHMENT(consumer: TagConsumer<*>) :
	HTMLTag(
		tagName = "ri:attachment",
		consumer = consumer,
		initialAttributes = emptyMap(),
		inlineTag = false,
		emptyTag = false
	), HtmlBlockTag {
	var riFileName: String
		get() = attributes["ri:filename"]!!
		set(value) {
			attributes["ri:filename"] = value
		}
}
