package by.dev.madhead.doktor.model

import java.io.Serializable

const val FRONTMATTER_TITLE = "title"
const val FRONTMATTER_PARENT = "parent"

data class FrontMatter(
	val title: String,
	val parent: String?
) : Serializable
