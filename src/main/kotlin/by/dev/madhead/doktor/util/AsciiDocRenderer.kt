package by.dev.madhead.doktor.util

import by.dev.madhead.doktor.model.FrontMatter
import by.dev.madhead.doktor.model.RenderedDok
import com.vladsch.flexmark.ext.yaml.front.matter.AbstractYamlFrontMatterVisitor
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet
import hudson.remoting.VirtualChannel
import jenkins.SlaveToMasterFileCallable
import java.io.File

class AsciiDocRenderer : SlaveToMasterFileCallable<RenderedDok>() {
	override fun invoke(file: File, channel: VirtualChannel?): RenderedDok {
		return RenderedDok("", FrontMatter(null))
	}
}
