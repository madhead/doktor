package by.dev.madhead.doktor.util

import by.dev.madhead.doktor.model.DoktorConfig
import hudson.FilePath
import hudson.model.TaskListener

fun diagnose(doktorConfig: DoktorConfig, workspace: FilePath, taskListener: TaskListener) {
	workspace
		.actAsync(WorkspaceDokLister(doktorConfig))

		.get()
		.forEach {
			taskListener.logger.println("${it}")
		}

//		workspace
//			.actAsync(WorkspaceDokLister(doktorConfig))
//			.toSingle()
//			.flatMapObservable { it.toObservable() }
//			.map {
//				when (it.markup) {
//					Markup.MARKDOWN -> {
//						it.filePath.actAsync(object : SlaveToMasterFileCallable<String>() {
//							override fun invoke(file: File, channel: VirtualChannel?): String {
//								val parser = Parser.builder().build()
//								val htmlRenderer = HtmlRenderer.builder().build()
//
//								return htmlRenderer.render(parser.parse(file.readText()))
//							}
//						})
//					}
//					Markup.ASCIIDOC -> {
//						it.filePath.actAsync(object : SlaveToMasterFileCallable<String>() {
//							override fun invoke(file: File, channel: VirtualChannel?): String {
//								return "NOT SUPPORTED YET"
//							}
//						})
//					}
//				}
//			}
//			.flatMap { Observable.fromFuture(it) }
//			.doOnNext {
//				context.get(TaskListener::class.java)?.logger?.println(it)
//			}
//			.doOnComplete {
//				context.onSuccess(42)
//			}
//			.doOnError {
//				context.onFailure(it)
//			}
//			.subscribe()
}
