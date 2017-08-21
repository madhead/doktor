package by.dev.madhead.doktor.classic

import by.dev.madhead.doktor.Messages
import by.dev.madhead.doktor.config.ConfluenceServers
import by.dev.madhead.doktor.model.DescribableString
import by.dev.madhead.doktor.model.DoktorStepConfig
import by.dev.madhead.doktor.model.Markup.ASCIIDOC
import by.dev.madhead.doktor.model.Markup.MARKDOWN
import hudson.AbortException
import hudson.Extension
import hudson.Launcher
import hudson.model.AbstractBuild
import hudson.model.AbstractProject
import hudson.model.BuildListener
import hudson.tasks.BuildStepDescriptor
import hudson.tasks.Builder
import hudson.util.ListBoxModel
import jenkins.model.GlobalConfiguration
import org.kohsuke.stapler.DataBoundConstructor

class DoktorStep
@DataBoundConstructor
constructor(
	val server: String,
	var markdownIncludePatterns: List<DescribableString>?,
	var markdownExcludePatterns: List<DescribableString>?,
	var asciidocIncludePatterns: List<DescribableString>?,
	var asciidocExcludePatterns: List<DescribableString>?
) : Builder() {
	override fun perform(build: AbstractBuild<*, *>, launcher: Launcher, listener: BuildListener): Boolean {
		val workspace = build.getWorkspace() ?: throw AbortException("Doktor requires a workspace to operate")
		val doktorStepConfig = DoktorStepConfig(
			server,
			mapOf(
				MARKDOWN to Pair(markdownIncludePatterns?.map { it.value } ?: emptyList(), markdownExcludePatterns?.map { it.value } ?: emptyList()),
				ASCIIDOC to Pair(asciidocIncludePatterns?.map { it.value } ?: emptyList(), asciidocExcludePatterns?.map { it.value } ?: emptyList())
			)
		)

		return true
	}

	@Extension
	class DoktorStepDescriptor : BuildStepDescriptor<Builder>() {
		override fun getDisplayName() = Messages.doktor_classic_DoktorStep_displayName()

		override fun isApplicable(jobType: Class<out AbstractProject<*, *>>): Boolean {
			return true
		}

		fun doFillServerItems(): ListBoxModel {
			val result = ListBoxModel()

			GlobalConfiguration.all().get(ConfluenceServers::class.java)?.servers?.forEach {
				result.add("${it.name} (${it.url})", it.name)
			}

			return result
		}
	}
}
