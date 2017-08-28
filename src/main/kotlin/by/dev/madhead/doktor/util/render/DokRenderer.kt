package by.dev.madhead.doktor.util.render

import by.dev.madhead.doktor.Messages
import by.dev.madhead.doktor.model.Markup
import by.dev.madhead.doktor.model.RenderedDok
import hudson.model.TaskListener
import hudson.remoting.VirtualChannel
import jenkins.SlaveToMasterFileCallable
import java.io.File

class DokRenderer(
	val markup: Markup,
	val taskListener: TaskListener
) : SlaveToMasterFileCallable<RenderedDok>() {
	override fun invoke(file: File, channel: VirtualChannel?): RenderedDok {
		taskListener.logger.println(Messages.doktor_util_render_DokRenderer_rendering(markup, file))

		return RenderedDok(file.absolutePath, markup.render(file.readText()))
	}
}
