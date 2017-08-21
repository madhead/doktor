package by.dev.madhead.doktor.util

import by.dev.madhead.doktor.model.DoktorConfig
import by.dev.madhead.doktor.model.Markup.ASCIIDOC
import by.dev.madhead.doktor.model.Markup.MARKDOWN
import com.vladsch.flexmark.ext.yaml.front.matter.AbstractYamlFrontMatterVisitor
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet
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
				MARKDOWN -> it.filePath.actAsync(MarkdownRenderer())
				ASCIIDOC -> it.filePath.actAsync(AsciiDocRenderer())
			}
		}
		.flatMap { Observable.fromFuture(it) }
		.blockingForEach {
			taskListener.logger?.println(it)
		}
}
