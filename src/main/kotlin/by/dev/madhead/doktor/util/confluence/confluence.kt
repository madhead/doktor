package by.dev.madhead.doktor.util.confluence

import by.dev.madhead.doktor.model.RenderedDok
import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import by.dev.madhead.doktor.model.confluence.*
import hudson.model.TaskListener
import io.reactivex.Maybe
import io.reactivex.Single

fun upload(confluenceServer: ResolvedConfluenceServer, renderedDok: RenderedDok, taskListener: TaskListener): Maybe<CreatePageResponse> {
	return findPage(confluenceServer, renderedDok.content.frontMatter.title)
		.flatMap {
			taskListener.logger.println("Deleting existing page ${it.id}")

			deletePage(confluenceServer, it.id)
				.flatMapMaybe {
					Maybe.empty<CreatePageResponse>()
				}
		}
		.switchIfEmpty(
			Single
				.just(renderedDok.content.frontMatter.parent.isNullOrEmpty())
				.flatMapMaybe {
					if (it) {
						taskListener.logger.println("Creating new top level page from ${renderedDok.filePath}")

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
						).toMaybe()
					} else {
						taskListener.logger.println("Resolving parent id for ${renderedDok.filePath} (parent title is ${renderedDok.content.frontMatter.parent})")

						findPage(confluenceServer, renderedDok.content.frontMatter.parent!!)
							.map {
								taskListener.logger.println("Resolved parent id for ${renderedDok.filePath}: ${it.id}")
								listOf(it.id)
							}
							.switchIfEmpty(
								Maybe.just(emptyList())
							)
							.flatMap {
								if (it.isNotEmpty()) {
									taskListener.logger.println("Creating new child page from ${renderedDok.filePath} (parent is ${renderedDok.content.frontMatter.parent} [${it}])")

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
											ancestors = it.map { ContentReference(it) }
										)
									).toMaybe()
								} else {
									taskListener.logger.println("Cannot resolve parent id for ${renderedDok.filePath}, the page will not be created!")

									Maybe.empty<CreatePageResponse>()
								}
							}
					}
				}
		)
}
