package by.dev.madhead.doktor.model.confluence

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Reader

data class FindPageResponse(
	val results: List<ContentReference>,
	val start: Int,
	val limit: Int,
	val size: Int
) {
	class Deserializer : ResponseDeserializable<FindPageResponse> {
		override fun deserialize(reader: Reader) = Gson().fromJson(reader, FindPageResponse::class.java)
	}
}
