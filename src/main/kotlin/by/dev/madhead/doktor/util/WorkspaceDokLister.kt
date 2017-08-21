package by.dev.madhead.doktor.util

import by.dev.madhead.doktor.model.Dok
import by.dev.madhead.doktor.model.DoktorConfig
import hudson.FilePath
import hudson.remoting.VirtualChannel
import jenkins.SlaveToMasterFileCallable
import java.io.File
import java.nio.file.Files

class WorkspaceDokLister(val doktorConfig: DoktorConfig) : SlaveToMasterFileCallable<List<Dok>>() {
	override fun invoke(file: File, channel: VirtualChannel?): List<Dok> {
		val workspace = file.toPath()
		val result = mutableListOf<Dok>()

		Files.walkFileTree(
			workspace,
			WorkspaceFileVisitor(
				workspace,
				doktorConfig.markupPatterns
			) { path, markup ->
				result.add(
					Dok(
						FilePath(channel, path.toString()),
						markup
					)
				)
			}
		)

		return result
	}
}
