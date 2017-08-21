package by.dev.madhead.doktor.pipeline

import by.dev.madhead.doktor.model.DoktorStepConfig
import by.dev.madhead.doktor.model.Markup.ASCIIDOC
import by.dev.madhead.doktor.model.Markup.MARKDOWN
import by.dev.madhead.doktor.util.WorkspaceDokLister
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import hudson.FilePath
import hudson.model.TaskListener
import hudson.remoting.VirtualChannel
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.toSingle
import jenkins.SlaveToMasterFileCallable
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.jenkinsci.plugins.workflow.steps.StepExecution
import java.io.File
import java.io.Serializable

class DoktorStepExecution(context: StepContext, val doktorStepConfig: DoktorStepConfig) : StepExecution(context), Serializable {
	companion object {
		private val serialVersionUID = 1L
	}

	override fun start(): Boolean {
		val workspace = context.get(FilePath::class.java)!!

		workspace
			.actAsync(WorkspaceDokLister(doktorStepConfig))
			.toSingle()
			.flatMapObservable { it.toObservable() }
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
								return "NOT SUPPORTED YET"
							}
						})
					}
				}
			}
			.flatMap { Observable.fromFuture(it) }
			.doOnNext {
				context.get(TaskListener::class.java)?.logger?.println(it)
			}
			.doOnComplete {
				context.onSuccess(42)
			}
			.doOnError {
				context.onFailure(it)
			}
			.subscribe()

		return false
	}

	override fun stop(cause: Throwable) {
		TODO("not implemented")
	}
}
