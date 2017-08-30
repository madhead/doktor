package by.dev.madhead.doktor.model

import hudson.FilePath

data class Dok(
	val filePath: FilePath,
	val markup: Markup
)

data class RenderedContent(
	val markup: Markup,
	val content: String,
	val frontMatter: FrontMatter
)

data class RenderedDok(
	val filePath: String,
	val content: RenderedContent
)
