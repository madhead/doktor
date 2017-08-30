package by.dev.madhead.doktor.model.confluence

import com.google.gson.Gson

data class CreatePageRequest(
	val type: String = "page",
	val title: String,
	val ancestors: List<ContentReference>? = null,
	val space: SpaceReference,
	val body: Body
) {
	fun asJSON() = Gson().toJson(this)
}
