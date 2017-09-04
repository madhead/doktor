package by.dev.madhead.doktor.confluence

import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import by.dev.madhead.doktor.model.confluence.ContentReference
import by.dev.madhead.doktor.model.confluence.FindPageResponse
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.rx.rx_object
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import io.reactivex.Maybe
import java.net.URL

fun findPage(confluenceServer: ResolvedConfluenceServer, title: String): Maybe<ContentReference> {
	return URL(URL(confluenceServer.url), "/rest/api/content").toString()
		.httpGet(
			listOf(
				"spaceKey" to confluenceServer.space,
				"title" to title
			)
		)
		.apply {
			if (!confluenceServer.user.isNullOrBlank()) {
				authenticate(confluenceServer.user!!, confluenceServer.password ?: "")
			}
		}
		.rx_object(FindPageResponse.Deserializer())
		.flatMapMaybe {
			when (it) {
				is Result.Success -> {
					if (it.value.size > 0) {
						Maybe.just(it.value.results[0])
					} else {
						Maybe.empty<ContentReference>()
					}
				}
				is Result.Failure -> {
					try {
						Maybe.error<ContentReference>(Gson().fromJson(it.error.response.data.toString(Charsets.UTF_8), ConfluenceException::class.java))
					} catch (e: Throwable) {
						Maybe.error<ContentReference>(ConfluenceException(it.error.message ?: it.error.exception.message ?: it.error.response.httpResponseMessage))
					}
				}
			}
		}
}
