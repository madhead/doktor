package by.dev.madhead.doktor.model.confluence

import com.google.gson.Gson

data class Label(
	val name: String,
	val prefix: String = "global"
)

typealias Labels = List<Label>

fun Labels.asJSON() = Gson().toJson(this)!!
