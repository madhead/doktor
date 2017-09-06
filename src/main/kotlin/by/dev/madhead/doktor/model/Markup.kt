package by.dev.madhead.doktor.model

import by.dev.madhead.doktor.render.asciiDoc
import by.dev.madhead.doktor.render.markdown

typealias RenderFunction = (String) -> RenderedContent

enum class Markup(val render: RenderFunction) {
	MARKDOWN(::markdown),
	ASCIIDOC(::asciiDoc)
}
