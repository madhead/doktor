package by.dev.madhead.doktor.pipeline

import by.dev.madhead.doktor.model.DoktorConfig
import by.dev.madhead.doktor.util.diagnose
import hudson.AbortException
import hudson.FilePath
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.jenkinsci.plugins.workflow.steps.StepExecution
import java.io.Serializable

class DoktorStepExecution(context: StepContext, val doktorConfig: DoktorConfig) : StepExecution(context), Serializable {
	companion object {
		private val serialVersionUID = 1L
	}

	override fun start(): Boolean {
		diagnose(
			doktorConfig,
			context.get(FilePath::class.java) ?: throw AbortException("Doktor requires a workspace to operate"),
			context.get(TaskListener::class.java) ?: throw AbortException("Doktor requires a TaskListener to operate")
		) // TODO: Add callbacks, make it async

		// TODO
		context.onSuccess(42)

		return true
	}

	override fun stop(cause: Throwable) {
		TODO("not implemented")
	}
}
