package by.dev.madhead.doktor.model.confluence.macros

import kotlinx.html.HTMLTag
import kotlinx.html.HtmlBlockTag
import kotlinx.html.TagConsumer

class RI_URL(consumer: TagConsumer<*>, url: String) :
	HTMLTag(
		tagName = "ri:url",
		consumer = consumer,
		initialAttributes = mapOf("ri:value" to url),
		inlineTag = false,
		emptyTag = false
	), HtmlBlockTag
