package by.dev.madhead.doktor.confluence

import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import by.dev.madhead.doktor.model.confluence.CreatePageRequest
import by.dev.madhead.doktor.model.confluence.CreatePageResponse
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.rx.rx_object
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import io.reactivex.Single
import java.net.URL

fun createPage(confluenceServer: ResolvedConfluenceServer, createPageRequest: CreatePageRequest): Single<CreatePageResponse> {
	return URL(URL(confluenceServer.url).toExternalForm() + "/rest/api/content").toString()
		.httpPost()
		.apply {
			if (!confluenceServer.user.isNullOrBlank()) {
				authenticate(confluenceServer.user!!, confluenceServer.password ?: "")
			}
		}
		.header("Content-Type" to "application/json")
		.body(createPageRequest.asJSON())
		.rx_object(CreatePageResponse.Deserializer())
		.flatMap {
			when (it) {
				is Result.Success -> Single.just(it.value)
				is Result.Failure -> {
					try {
						val exception = Gson().fromJson(it.error.response.data.toString(Charsets.UTF_8), ConfluenceException::class.java)

						if (!exception.message.isBlank()) {
							Single.error<CreatePageResponse>(exception)
						} else {
							throw IllegalArgumentException()
						}
					} catch (e: Throwable) {
						Single.error<CreatePageResponse>(ConfluenceException(it.error.exception.message ?: it.error.message ?: it.error.response.responseMessage))
					}
				}
			}
		}
}
