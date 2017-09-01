package by.dev.madhead.doktor.model.confluence

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Reader

data class CreatePageResponse(
	val id: String
) {
	class Deserializer : ResponseDeserializable<CreatePageResponse> {
		override fun deserialize(reader: Reader) = Gson().fromJson(reader, CreatePageResponse::class.java)
	}
}
