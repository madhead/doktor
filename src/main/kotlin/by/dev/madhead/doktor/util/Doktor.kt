package by.dev.madhead.doktor.util

import by.dev.madhead.doktor.model.DoktorConfig
import by.dev.madhead.doktor.model.Markup.ASCIIDOC
import by.dev.madhead.doktor.model.Markup.MARKDOWN
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import hudson.FilePath
import hudson.model.TaskListener
import hudson.remoting.VirtualChannel
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.toSingle
import jenkins.SlaveToMasterFileCallable
import java.io.File

fun diagnose(doktorConfig: DoktorConfig, workspace: FilePath, taskListener: TaskListener) {
	workspace
		.actAsync(WorkspaceDokLister(doktorConfig))
		.toSingle()
		.toObservable()
		.flatMap { it.toObservable() }
		.map {
			when (it.markup) {
				MARKDOWN -> {
					it.filePath.actAsync(object : SlaveToMasterFileCallable<String>() {
						override fun invoke(file: File, channel: VirtualChannel?): String {
							val parser = Parser.builder().build()
							val htmlRenderer = HtmlRenderer.builder().build()

							return htmlRenderer.render(parser.parse(file.readText()))
						}
					})
				}
				ASCIIDOC -> {
					it.filePath.actAsync(object : SlaveToMasterFileCallable<String>() {
						override fun invoke(file: File, channel: VirtualChannel?): String {
							return file.readText()
						}
					})
				}
			}
		}
		.flatMap { Observable.fromFuture(it) }
		.blockingForEach {
			taskListener.logger?.println(it)
		}
}
