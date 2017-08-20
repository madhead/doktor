package by.dev.madhead.doktor.pipeline

import by.dev.madhead.doktor.Messages
import by.dev.madhead.doktor.config.ConfluenceServers
import by.dev.madhead.doktor.model.Markup.ASCIIDOC
import by.dev.madhead.doktor.model.Markup.MARKDOWN
import by.dev.madhead.doktor.model.DoktorStepConfig
import hudson.Extension
import hudson.FilePath
import hudson.util.ListBoxModel
import jenkins.model.GlobalConfiguration
import org.jenkinsci.plugins.workflow.steps.Step
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.jenkinsci.plugins.workflow.steps.StepDescriptor
import org.kohsuke.stapler.DataBoundConstructor

class DoktorStep
@DataBoundConstructor
constructor(
	val server: String,
	var markdownIncludePatterns: List<String>?,
	var markdownExcludePatterns: List<String>?,
	var asciidocIncludePatterns: List<String>?,
	var asciidocExcludePatterns: List<String>?
) : Step() {
	override fun start(context: StepContext) = DoktorStepExecution(
		context,
		DoktorStepConfig(
			server,
			mapOf(
				MARKDOWN to Pair(markdownIncludePatterns ?: emptyList(), markdownExcludePatterns ?: emptyList()),
				ASCIIDOC to Pair(asciidocIncludePatterns ?: emptyList(), asciidocExcludePatterns ?: emptyList())
			)
		)
	)

	@Extension
	class DoktorStepDescriptor : StepDescriptor() {
		override fun getFunctionName() = "doktor"

		override fun getDisplayName() = Messages.doktor_pipeline_DoktorStep_displayName()

		override fun getRequiredContext() = setOf(FilePath::class.java)

		fun doFillServerItems(): ListBoxModel {
			val result = ListBoxModel()

			GlobalConfiguration.all().get(ConfluenceServers::class.java)?.servers?.forEach {
				result.add("${it.name} (${it.url})", it.name)
			}

			return result
		}
	}
}
