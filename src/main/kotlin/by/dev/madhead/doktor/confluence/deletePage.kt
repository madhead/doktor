package by.dev.madhead.doktor.confluence

import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.rx.rx_response
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import io.reactivex.Completable
import java.net.URL

fun deletePage(confluenceServer: ResolvedConfluenceServer, id: String): Completable {
	return URL(URL(confluenceServer.url).toExternalForm() + "/rest/api/content/${id}").toString()
		.httpDelete()
		.apply {
			if (!confluenceServer.user.isNullOrBlank()) {
				authenticate(confluenceServer.user!!, confluenceServer.password ?: "")
			}
		}
		.rx_response()
		.flatMapCompletable { (_, result) ->
			when (result) {
				is Result.Success -> Completable.complete()
				is Result.Failure -> {
					try {
						val exception = Gson().fromJson(result.error.response.data.toString(Charsets.UTF_8), ConfluenceException::class.java)

						if (!exception.message.isBlank()) {
							Completable.error(exception)
						} else {
							throw IllegalArgumentException()
						}
					} catch (e: Throwable) {
						Completable.error(ConfluenceException(result.error.exception.message ?: result.error.message ?: result.error.response.responseMessage))
					}
				}
			}
		}
}
