package by.dev.madhead.doktor.util.confluence

import by.dev.madhead.doktor.model.RenderedDok
import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import by.dev.madhead.doktor.model.confluence.*
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.rx.rx_object
import com.github.kittinunf.fuel.rx.rx_response
import com.github.kittinunf.result.Result
import io.reactivex.Maybe
import io.reactivex.Single
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
				is Result.Failure -> Maybe.error<ContentReference>(it.error)
			}
		}
}

fun upload(confluenceServer: ResolvedConfluenceServer, renderedDok: RenderedDok): Single<Pair<Response, Result<ByteArray, FuelError>>>? {
	return URL(URL(confluenceServer.url), "/rest/api/content").toString()
		.httpGet(
			listOf(
				"spaceKey" to confluenceServer.space,
				"title" to renderedDok.content.frontMatter.title
			)
		)
		.apply {
			if (!confluenceServer.user.isNullOrBlank()) {
				authenticate(confluenceServer.user!!, confluenceServer.password ?: "")
			}
		}
		.rx_object(FindPageResponse.Deserializer())
		.flatMap { result ->
			when (result) {
				is Result.Success -> {
					if (result.value.size == 0) {
						// 1. Find parent
						// 2. Craft request
						// 3. Fire
						URL(URL(confluenceServer.url), "/rest/api/content").toString()
							.httpPost()
							.apply {
								if (!confluenceServer.user.isNullOrBlank()) {
									authenticate(confluenceServer.user!!, confluenceServer.password ?: "")
								}
							}
							.header("Content-Type" to "application/json")
							.body(
								CreatePageRequest(
									title = renderedDok.content.frontMatter.title,
									space = SpaceReference(confluenceServer.space),
									body = Body(
										storage = Storage(
											value = renderedDok.content.content
										)
									)
								).asJSON()
							)
							.rx_response()
					} else {
						// 1. Find parent
						// 2. Parent changed?
						// 3. Craft request
						// 4. Fire
						URL(URL(confluenceServer.url), "/rest/api/content").toString()
							.httpPost()
							.apply {
								if (!confluenceServer.user.isNullOrBlank()) {
									authenticate(confluenceServer.user!!, confluenceServer.password ?: "")
								}
							}
							.body(
								CreatePageRequest(
									title = renderedDok.content.frontMatter.title,
									space = SpaceReference(confluenceServer.space),
									body = Body(
										storage = Storage(
											value = renderedDok.content.content
										)
									)
								).asJSON()
							)
							.rx_response()
					}
				}
				is Result.Failure -> {
					TODO()
				}
			}
		}
}
