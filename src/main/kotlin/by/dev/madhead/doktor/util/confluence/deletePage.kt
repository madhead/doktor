package by.dev.madhead.doktor.util.confluence

import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.rx.rx_response
import com.github.kittinunf.result.Result
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
				is Result.Failure -> Single.error(result.error)
			}
		}
}
