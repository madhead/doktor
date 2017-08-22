package by.dev.madhead.doktor.pipeline

import by.dev.madhead.doktor.Messages
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
			context.get(FilePath::class.java) ?: throw AbortException(Messages.doktor_hudson_AbortException_AbortException_workspaceRequired()),
			context.get(TaskListener::class.java) ?: throw AbortException(Messages.doktor_hudson_AbortException_AbortException_taskListenerRequired())
		)
			.subscribe(
				{},
				{ context.onFailure(it) },
				{ context.onSuccess(null) }
			)

		return false
	}

	override fun stop(cause: Throwable) {
		TODO("not implemented")
	}
}
