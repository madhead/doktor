package by.dev.madhead.doktor.model.confluence

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Reader

data class CreateAttachmentResponse(
	val results: List<ContentReference>
) {
	class Deserializer : ResponseDeserializable<CreateAttachmentResponse> {
		override fun deserialize(reader: Reader) = Gson().fromJson(reader, CreateAttachmentResponse::class.java)
	}
}
