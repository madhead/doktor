package by.dev.madhead.doktor.util.confluence

import by.dev.madhead.doktor.model.RenderedDok
import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import by.dev.madhead.doktor.model.confluence.*
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.rx.rx_object
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

fun upload(confluenceServer: ResolvedConfluenceServer, renderedDok: RenderedDok): Single<CreatePageResponse> {
	return findPage(confluenceServer, renderedDok.content.frontMatter.title)
		.isEmpty
		.flatMap {
			if (it) {
				// New page

				Single
					.just(renderedDok.content.frontMatter.parent.isNullOrEmpty())
					.flatMap {
						if (it) {
							// Top level page

							createPage(
								confluenceServer,
								CreatePageRequest(
									title = renderedDok.content.frontMatter.title,
									space = SpaceReference(confluenceServer.space),
									body = Body(
										storage = Storage(
											value = renderedDok.content.content
										)
									)
								)
							)
						} else {
							// Child page

							findPage(confluenceServer, renderedDok.content.frontMatter.parent!!)
								.flatMapSingleElement {
									createPage(
										confluenceServer,
										CreatePageRequest(
											title = renderedDok.content.frontMatter.title,
											space = SpaceReference(confluenceServer.space),
											ancestors = listOf(it),
											body = Body(
												storage = Storage(
													value = renderedDok.content.content
												)
											)
										)
									)
								}
								.toSingle()
						}
					}
			} else {
				// Update existing page

				TODO()
			}
		}
}
