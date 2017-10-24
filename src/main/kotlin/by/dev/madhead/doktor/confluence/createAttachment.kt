package by.dev.madhead.doktor.confluence

import by.dev.madhead.doktor.model.Attachment
import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import by.dev.madhead.doktor.model.confluence.CreateAttachmentResponse
import com.github.kittinunf.fuel.core.Blob
import com.github.kittinunf.fuel.httpUpload
import com.github.kittinunf.fuel.rx.rx_object
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import io.reactivex.Single
import java.net.URL

fun createAttachment(confluenceServer: ResolvedConfluenceServer, id: String, attachment: Attachment): Single<CreateAttachmentResponse> {
	return URL(URL(confluenceServer.url), "/rest/api/content/$id/child/attachment").toString()
		.httpUpload()
		.apply {
			if (!confluenceServer.user.isNullOrBlank()) {
				authenticate(confluenceServer.user!!, confluenceServer.password ?: "")
			}
		}
		.blob { request, _ ->
			request.name = "file"
			Blob(attachment.fileName, attachment.bytes.size.toLong(), { attachment.bytes.inputStream() })
		}
		.header("X-Atlassian-Token" to "nocheck")
		.rx_object(CreateAttachmentResponse.Deserializer())
		.flatMap {
			when (it) {
				is Result.Success -> Single.just(it.value)
				is Result.Failure -> {
					try {
						val exception = Gson().fromJson(it.error.response.data.toString(Charsets.UTF_8), ConfluenceException::class.java)

						if (!exception.message.isBlank()) {
							Single.error<CreateAttachmentResponse>(exception)
						} else {
							throw IllegalArgumentException()
						}
					} catch (e: Throwable) {
						Single.error<CreateAttachmentResponse>(ConfluenceException(it.error.exception.message ?: it.error.message ?: it.error.response.responseMessage))
					}
				}
			}
		}
}
