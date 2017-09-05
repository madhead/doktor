package by.dev.madhead.doktor.model

import java.io.Serializable

const val FRONTMATTER_TITLE = "title"
const val FRONTMATTER_PARENT = "parent"
const val FRONTMATTER_LABELS = "labels"

data class FrontMatter(
	val title: String,
	val parent: String?,
	val labels: List<String>
) : Serializable
