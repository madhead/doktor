package by.dev.madhead.doktor.model

import by.dev.madhead.doktor.util.render.asciiDoc
import by.dev.madhead.doktor.util.render.markdown

typealias RenderFunction = (String) -> RenderedContent

enum class Markup(val render: RenderFunction) {
	MARKDOWN(::markdown),
	ASCIIDOC(::asciiDoc)
}
