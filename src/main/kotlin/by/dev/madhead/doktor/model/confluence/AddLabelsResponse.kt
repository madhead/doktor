package by.dev.madhead.doktor.model.confluence

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Reader

data class AddLabelsResponse(
	val results: Labels,
	val start: Int,
	val limit: Int,
	val size: Int

) {
	class Deserializer : ResponseDeserializable<AddLabelsResponse> {
		override fun deserialize(reader: Reader) = Gson().fromJson(reader, AddLabelsResponse::class.java)
	}
}
