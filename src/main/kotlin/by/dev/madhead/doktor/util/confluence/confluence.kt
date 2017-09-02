package by.dev.madhead.doktor.util.confluence

import by.dev.madhead.doktor.model.RenderedDok
import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import by.dev.madhead.doktor.model.confluence.*
import io.reactivex.Single

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
								.map {
									listOf(it.id)
								}
								.switchIfEmpty {
									emptyList<String>()
								}
								.flatMapSingleElement {
									createPage(
										confluenceServer,
										CreatePageRequest(
											title = renderedDok.content.frontMatter.title,
											space = SpaceReference(confluenceServer.space),
											body = Body(
												storage = Storage(
													value = renderedDok.content.content
												)
											),
											ancestors = if (it.isNotEmpty()) {
												it.map { ContentReference(it) }
											} else {
												null
											}
										)
									)
								}
								.toSingle()
						}
					}
			} else {
				// Update existing page

				// TODO
				findPage(confluenceServer, renderedDok.content.frontMatter.title)
					.flatMapSingle {
						deletePage(confluenceServer, it.id)
					}
					.flatMap {
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
					}
			}
		}
}
