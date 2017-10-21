package by.dev.madhead.doktor.model

import by.dev.madhead.doktor.render.asciiDoc
import by.dev.madhead.doktor.render.markdown
import java.io.File

typealias RenderFunction = (File) -> RenderedContent

enum class Markup(val render: RenderFunction) {
	MARKDOWN(::markdown),
	ASCIIDOC(::asciiDoc)
}
