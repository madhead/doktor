package by.dev.madhead.doktor.confluence

import by.dev.madhead.doktor.model.RenderedDok
import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import by.dev.madhead.doktor.model.confluence.*
import hudson.model.TaskListener
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

fun upload(confluenceServer: ResolvedConfluenceServer, renderedDok: RenderedDok, taskListener: TaskListener): Completable {
	taskListener.logger.println("Checking if a page already exists for ${renderedDok.filePath} ('${renderedDok.content.frontMatter.title}')")

	return findPage(confluenceServer, renderedDok.content.frontMatter.title)
		.flatMap {
			taskListener.logger.println("Deleting existing page (ID: ${it.id}) for ${renderedDok.filePath} ('${renderedDok.content.frontMatter.title}')")

			deletePage(confluenceServer, it.id)
				.toMaybe<CreatePageResponse>()
		}
		.switchIfEmpty(
			Single
				.just(renderedDok.content.frontMatter.parent.isNullOrEmpty())
				.flatMapMaybe {
					if (it) {
						taskListener.logger.println("Creating new top level page from ${renderedDok.filePath} ('${renderedDok.content.frontMatter.title}')")

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
						).doOnSuccess {
							taskListener.logger.println("Created new top level page from ${renderedDok.filePath} ('${renderedDok.content.frontMatter.title}'). ID: ${it.id}")
						}.toMaybe()
					} else {
						taskListener.logger.println("Resolving parent ID for ${renderedDok.filePath} ('${renderedDok.content.frontMatter.title}'), parent is '${renderedDok.content.frontMatter.parent}'")

						findPage(confluenceServer, renderedDok.content.frontMatter.parent!!)
							.map {
								taskListener.logger.println("Resolved parent ID for ${renderedDok.filePath} ('${renderedDok.content.frontMatter.title}'): ${it.id}")
								listOf(it.id)
							}
							.switchIfEmpty(
								Maybe.just(emptyList())
							)
							.flatMap {
								if (it.isNotEmpty()) {
									taskListener.logger.println("Creating new child page from ${renderedDok.filePath} ('${renderedDok.content.frontMatter.title}'), parent is '${renderedDok.content.frontMatter.parent}' (${it})")

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
									).doOnSuccess {
										taskListener.logger.println("Created new child page from ${renderedDok.filePath} ('${renderedDok.content.frontMatter.title}'), parent is '${renderedDok.content.frontMatter.parent}' (${it}). ID: ${it.id}")
									}.toMaybe()
								} else {
									taskListener.error("Cannot resolve parent ID for ${renderedDok.filePath} ('${renderedDok.content.frontMatter.title}'), the page will not be created!")

									Maybe.empty<CreatePageResponse>()
								}
							}
					}
				}
		)
		.flatMapCompletable {
			if (renderedDok.content.frontMatter.labels.isNotEmpty()) {
				taskListener.logger.println("Adding ${renderedDok.content.frontMatter.labels} labels for ${renderedDok.filePath} ('${renderedDok.content.frontMatter.title}', ${it.id})")

				addLabels(confluenceServer, it.id, renderedDok.content.frontMatter.labels.map { Label(name = it) })
					.toCompletable()
			} else {
				Completable.complete()
			}
		}
}
