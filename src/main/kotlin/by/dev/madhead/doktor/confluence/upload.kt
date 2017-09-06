package by.dev.madhead.doktor.confluence

import by.dev.madhead.doktor.Messages
import by.dev.madhead.doktor.model.RenderedDok
import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import by.dev.madhead.doktor.model.confluence.*
import hudson.model.TaskListener
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

fun upload(confluenceServer: ResolvedConfluenceServer, renderedDok: RenderedDok, taskListener: TaskListener): Completable {
	taskListener.logger.println(Messages.doktor_confluence_upload_checkingExistence(renderedDok.filePath, renderedDok.content.frontMatter.title))

	return findPage(confluenceServer, renderedDok.content.frontMatter.title)
		.flatMap {
			taskListener.logger.println(Messages.doktor_confluence_upload_deletingExistingPage(it.id, renderedDok.filePath, renderedDok.content.frontMatter.title))

			deletePage(confluenceServer, it.id)
				.toMaybe<CreatePageResponse>()
		}
		.switchIfEmpty(
			Single
				.just(renderedDok.content.frontMatter.parent.isNullOrEmpty())
				.flatMapMaybe {
					if (it) {
						taskListener.logger.println(Messages.doktor_confluence_upload_creatingNewTopLevelPage(renderedDok.filePath, renderedDok.content.frontMatter.title))

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
							taskListener.logger.println(Messages.doktor_confluence_upload_createdNewTopLevelPage(renderedDok.filePath, renderedDok.content.frontMatter.title, it.id))
						}.toMaybe()
					} else {
						taskListener.logger.println(Messages.doktor_confluence_upload_resolvingParent(renderedDok.filePath, renderedDok.content.frontMatter.title, renderedDok.content.frontMatter.parent))

						findPage(confluenceServer, renderedDok.content.frontMatter.parent!!)
							.map {
								taskListener.logger.println(Messages.doktor_confluence_upload_resolvedParent(renderedDok.filePath, renderedDok.content.frontMatter.title, it.id))
								listOf(it.id)
							}
							.switchIfEmpty(
								Maybe.just(emptyList())
							)
							.flatMap {
								if (it.isNotEmpty()) {
									taskListener.logger.println(Messages.doktor_confluence_upload_creatingNewChildPage(renderedDok.filePath, renderedDok.content.frontMatter.title, renderedDok.content.frontMatter.parent, it))

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
										taskListener.logger.println(Messages.doktor_confluence_upload_createdNewChildPage(renderedDok.filePath, renderedDok.content.frontMatter.title, renderedDok.content.frontMatter.parent, it, it.id))
									}.toMaybe()
								} else {
									taskListener.error(Messages.doktor_confluence_upload_cannotResolveParent(renderedDok.filePath, renderedDok.content.frontMatter.title))

									Maybe.empty<CreatePageResponse>()
								}
							}
					}
				}
		)
		.flatMapCompletable { (id) ->
			val labelsCompletable =
				if (renderedDok.content.frontMatter.labels.isNotEmpty()) {
					taskListener.logger.println(Messages.doktor_confluence_upload_addingLabels(renderedDok.content.frontMatter.labels, renderedDok.filePath, renderedDok.content.frontMatter.title, id))

					addLabels(confluenceServer, id, renderedDok.content.frontMatter.labels.map { Label(name = it) })
						.toCompletable()
						.doOnError {
							taskListener.error(Messages.doktor_confluence_upload_addingLabelsError(renderedDok.filePath, renderedDok.content.frontMatter.title, id))
						}
						.onErrorComplete()
				} else {
					Completable.complete()
				}
			val attachmentsCompletables =
				if (renderedDok.images.isNotEmpty()) {
					taskListener.logger.println(Messages.doktor_confluence_upload_uploadingAttachmnets(renderedDok.filePath, renderedDok.content.frontMatter.title, id))

					renderedDok.images.map {
						createAttachment(confluenceServer, id, it)
							.toCompletable()
							.doOnError {
								taskListener.error(Messages.doktor_confluence_upload_uploadingAttachmnetsError(renderedDok.filePath, renderedDok.content.frontMatter.title, id))
							}
							.onErrorComplete()
					}
				} else {
					listOf(Completable.complete())
				}

			Completable.mergeDelayError(
				listOf(labelsCompletable) + attachmentsCompletables
			).onErrorComplete()
		}
}
