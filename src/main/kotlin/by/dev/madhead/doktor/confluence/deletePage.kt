package by.dev.madhead.doktor.confluence

import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.rx.rx_response
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import io.reactivex.Single
import java.net.URL

fun deletePage(confluenceServer: ResolvedConfluenceServer, id: String): Single<Boolean> {
	return URL(URL(confluenceServer.url), "/rest/api/content/${id}").toString()
		.httpDelete()
		.apply {
			if (!confluenceServer.user.isNullOrBlank()) {
				authenticate(confluenceServer.user!!, confluenceServer.password ?: "")
			}
		}
		.rx_response()
		.flatMap { (_, result) ->
			when (result) {
				is Result.Success -> Single.just(true)
				is Result.Failure -> {
					try {
						Single.error<Boolean>(Gson().fromJson(result.error.response.data.toString(Charsets.UTF_8), ConfluenceException::class.java))
					} catch (e: Throwable) {
						Single.error<Boolean>(ConfluenceException(result.error.message ?: result.error.exception.message ?: result.error.response.httpResponseMessage))
					}
				}
			}
		}
}
