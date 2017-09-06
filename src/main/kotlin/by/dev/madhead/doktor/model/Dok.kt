package by.dev.madhead.doktor.model

import hudson.FilePath
import java.io.Serializable

data class Dok(
	val filePath: FilePath,
	val markup: Markup
) : Serializable

data class RenderedContent(
	val markup: Markup,
	val content: String,
	val frontMatter: FrontMatter
) : Serializable

data class RenderedDok(
	val filePath: String,
	val content: RenderedContent,
	val images: List<Attachment>
) : Serializable
