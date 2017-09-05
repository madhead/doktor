package by.dev.madhead.doktor.pipeline

import by.dev.madhead.doktor.Messages
import by.dev.madhead.doktor.model.DoktorConfig
import by.dev.madhead.doktor.util.diagnose
import hudson.AbortException
import hudson.FilePath
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution

class DoktorStepExecution(context: StepContext, val doktorConfig: DoktorConfig) : SynchronousNonBlockingStepExecution<Void>(context) {
	override fun run(): Void? {
		try {
			diagnose(
				doktorConfig,
				context.get(FilePath::class.java) ?: throw AbortException(Messages.doktor_hudson_AbortException_AbortException_workspaceRequired()),
				context.get(TaskListener::class.java) ?: throw AbortException(Messages.doktor_hudson_AbortException_AbortException_taskListenerRequired())
			).blockingAwait()

			return null
		} catch (exception: Throwable) {
			throw AbortException(exception.message)
		}
	}
}
