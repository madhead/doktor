package by.dev.madhead.doktor.pipeline

import hudson.Extension
import hudson.model.Run
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.steps.Step
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.jenkinsci.plugins.workflow.steps.StepDescriptor
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution
import org.kohsuke.stapler.DataBoundConstructor

class DoktorStep
@DataBoundConstructor
constructor(
	val message: String
) : Step() {
	override fun start(context: StepContext) = Execution(context)

	@Extension
	class DoktorStepDescriptor : StepDescriptor() {
		override fun getFunctionName() = "doktor"

		override fun getDisplayName() = "Publish documentation to Confluence"

		// TODO: Implement this method wisely
		override fun getRequiredContext() = setOf(Run::class.java)
	}

	inner class Execution(context: StepContext) : SynchronousStepExecution<Unit?>(context) {
		override fun run(): Unit? {
			return context.get(TaskListener::class.java)?.logger?.println(message)
		}
	}
}
