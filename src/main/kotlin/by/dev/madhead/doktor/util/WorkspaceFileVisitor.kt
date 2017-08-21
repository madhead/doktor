package by.dev.madhead.doktor.util

import by.dev.madhead.doktor.model.Markup
import by.dev.madhead.doktor.model.MarkupPatterns
import java.nio.file.FileVisitResult
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

class WorkspaceFileVisitor(
	val base: Path,
	val matchers: MarkupPatterns,
	val accept: (Path, Markup) -> Unit
) : SimpleFileVisitor<Path>() {
	override fun visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult {
		val relativePath = base.relativize(path)

		for ((markup, matchers) in matchers) {
			if ((matchers.first.map { path.fileSystem.getPathMatcher(it) }.any { it.matches(relativePath) })
				&& (matchers.second.map { path.fileSystem.getPathMatcher(it) }.none { it.matches(relativePath) })) {
				accept(path, markup)
			}
		}

		// TODO: Support for stopping
		return FileVisitResult.CONTINUE
	}
}
