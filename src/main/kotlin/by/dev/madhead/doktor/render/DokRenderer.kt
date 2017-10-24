package by.dev.madhead.doktor.render

import by.dev.madhead.doktor.Messages
import by.dev.madhead.doktor.model.Attachment
import by.dev.madhead.doktor.model.Markup
import by.dev.madhead.doktor.model.RenderedDok
import by.dev.madhead.doktor.model.confluence.macros.acImage
import by.dev.madhead.doktor.model.confluence.macros.riAttachment
import by.dev.madhead.doktor.model.confluence.macros.riUrl
import com.j256.simplemagic.ContentInfoUtil
import hudson.model.TaskListener
import hudson.remoting.VirtualChannel
import jenkins.SlaveToMasterFileCallable
import kotlinx.html.stream.appendHTML
import org.apache.commons.codec.digest.DigestUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import java.io.File
import java.net.URI

class DokRenderer(
	val markup: Markup,
	val taskListener: TaskListener
) : SlaveToMasterFileCallable<RenderedDok>() {
	override fun invoke(file: File, channel: VirtualChannel?): RenderedDok {
		taskListener.logger.println(Messages.doktor_render_DokRenderer_rendering(markup, file))

		val content = markup.render(file)
		val document = Jsoup.parseBodyFragment(wrap(content.content))

		document.outputSettings().syntax(Document.OutputSettings.Syntax.xml)

		val images = processImages(document, file)

		return RenderedDok(
			filePath = file.absolutePath,
			content = content.copy(content = document.body().html()),
			images = images
		)
	}

	private fun processImages(document: Document, file: File): List<Attachment> {
		val result = mutableListOf<Attachment>()
		val magic = ContentInfoUtil()

		document
			.getElementsByTag("img")
			.forEach {
				val src = it.attr("src")

				try {
					if (null == URI(src).host) {
						val bytes = file.resolveSibling(src).readBytes()
						val contentInfo = magic.findMatch(bytes)
						val fileName = DigestUtils.md5Hex(bytes) + if ((null == contentInfo) || (null == contentInfo.fileExtensions)) {
							".jpeg" // why not?
						} else {
							".${contentInfo.fileExtensions[0]}"
						}

						result.add(Attachment(fileName, bytes))
						it.replaceWith(Jsoup.parse(buildString {
							appendHTML(true).acImage {
								if (!it.attr("width").isNullOrBlank()) {
									acWidth = it.attr("width")
								}
								if (!it.attr("height").isNullOrBlank()) {
									acHeight = it.attr("height")
								}
								if (!it.attr("alt").isNullOrBlank()) {
									acAlt = it.attr("alt")
								}
								// Special support for Asciidoc image titles
								it.parents().find {
									it.attr("class")?.contains("imageblock") ?: false
								}?.let {
									it.children().find {
										it.attr("class")?.contains("title") ?: false
									}?.let {
										acTitle = it.html()
									}
								}
								riAttachment(fileName)
							}
						}, "", Parser.xmlParser()).child(0))
					} else {
						it.replaceWith(Jsoup.parse(buildString {
							appendHTML(true).acImage {
								if (!it.attr("width").isNullOrBlank()) {
									acWidth = it.attr("width")
								}
								if (!it.attr("height").isNullOrBlank()) {
									acHeight = it.attr("height")
								}
								if (!it.attr("alt").isNullOrBlank()) {
									acAlt = it.attr("alt")
								}
								// Special support for Asciidoc image titles
								it.parents().find {
									it.attr("class")?.contains("imageblock") ?: false
								}?.let {
									it.children().find {
										it.attr("class")?.contains("title") ?: false
									}?.let {
										acTitle = it.html()
									}
								}
								riUrl(src)
							}
						}, "", Parser.xmlParser()).child(0))
					}
				} catch (e: Throwable) {
					taskListener.error(Messages.doktor_render_DokRenderer_imageFailed(src))
				}
			}

		return result
	}

	private fun wrap(content: String): String {
		return """
			<div class="doktor">
				${content}
			</div>
		"""
	}
}
