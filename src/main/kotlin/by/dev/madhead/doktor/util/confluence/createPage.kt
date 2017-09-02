package by.dev.madhead.doktor.util.confluence

import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import by.dev.madhead.doktor.model.confluence.CreatePageRequest
import by.dev.madhead.doktor.model.confluence.CreatePageResponse
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.rx.rx_object
import com.github.kittinunf.result.Result
import io.reactivex.Single
import java.net.URL

fun createPage(confluenceServer: ResolvedConfluenceServer, createPageRequest: CreatePageRequest): Single<CreatePageResponse> {
	return URL(URL(confluenceServer.url), "/rest/api/content").toString()
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
				is Result.Failure -> Single.error(it.error)
			}
		}
}
