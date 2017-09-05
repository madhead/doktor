package by.dev.madhead.doktor.confluence

import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import by.dev.madhead.doktor.model.confluence.AddLabelsResponse
import by.dev.madhead.doktor.model.confluence.Labels
import by.dev.madhead.doktor.model.confluence.asJSON
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.rx.rx_object
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import io.reactivex.Single
import java.net.URL

fun addLabels(confluenceServer: ResolvedConfluenceServer, id: String, labels: Labels): Single<AddLabelsResponse> {
	return URL(URL(confluenceServer.url), "/rest/api/content/${id}/label").toString()
		.httpPost()
		.apply {
			if (!confluenceServer.user.isNullOrBlank()) {
				authenticate(confluenceServer.user!!, confluenceServer.password ?: "")
			}
		}
		.header("Content-Type" to "application/json")
		.body(labels.asJSON())
		.rx_object(AddLabelsResponse.Deserializer())
		.flatMap {
			when (it) {
				is Result.Success -> Single.just(it.value)
				is Result.Failure -> {
					try {
						Single.error<AddLabelsResponse>(Gson().fromJson(it.error.response.data.toString(Charsets.UTF_8), ConfluenceException::class.java))
					} catch (e: Throwable) {
						Single.error<AddLabelsResponse>(ConfluenceException(it.error.message ?: it.error.exception.message ?: it.error.response.httpResponseMessage))
					}
				}
			}
		}
}
