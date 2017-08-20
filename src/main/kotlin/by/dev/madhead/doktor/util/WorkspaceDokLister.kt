package by.dev.madhead.doktor.util

import by.dev.madhead.doktor.model.Dok
import by.dev.madhead.doktor.model.DoktorStepConfig
import hudson.FilePath
import hudson.remoting.VirtualChannel
import jenkins.SlaveToMasterFileCallable
import java.io.File
import java.nio.file.Files

class WorkspaceDokLister(val doktorStepConfig: DoktorStepConfig) : SlaveToMasterFileCallable<List<Dok>>() {
	override fun invoke(file: File, channel: VirtualChannel?): List<Dok> {
		val base = file.toPath()
		val result = mutableListOf<Dok>()

		Files.walkFileTree(
			base,
			WorkspaceFileVisitor(
				base,
				doktorStepConfig.markupPatterns
			) { path, markup ->
				result.add(Dok(FilePath(path.toFile()), markup))
			}
		)

		return result
	}
}
