package by.dev.madhead.doktor.model

import by.dev.madhead.doktor.util.render.asciiDoc
import by.dev.madhead.doktor.util.render.markdown
import hudson.FilePath

typealias RenderFunction = (String) -> RenderedDok

enum class Markup(val render: RenderFunction) {
	MARKDOWN(::markdown),
	ASCIIDOC(::asciiDoc)
}

data class Dok(
	val filePath: FilePath,
	val markup: Markup
)

const val CONFLUENCE_PATH = "confluencePath"

data class FrontMatter(
	val confluencePath: String?
)

data class RenderedDok(
	val content: String,
	val frontMatter: FrontMatter
)
