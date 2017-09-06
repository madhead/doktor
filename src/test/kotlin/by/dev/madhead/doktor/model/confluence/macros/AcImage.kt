package by.dev.madhead.doktor.model.confluence.macros

import kotlinx.html.stream.appendHTML
import org.testng.Assert
import org.testng.annotations.Test

class AcImage {
	@Test
	fun attachment() {
		Assert.assertEquals(
			buildString {
				appendHTML(true).acImage {
					riAttachment("028935e487e29a7c37fafdeec824946b112c71a0ff8781ff2ae6c9d909167c79.jpg")
				}
			},
			"<ac:image>\n  <ri:attachment ri:filename=\"028935e487e29a7c37fafdeec824946b112c71a0ff8781ff2ae6c9d909167c79.jpg\"></ri:attachment>\n</ac:image>\n",
			"Unexpected output")
	}

	@Test
	fun url() {
		Assert.assertEquals(
			buildString {
				appendHTML(true).acImage {
					riUrl("https://i.stack.imgur.com/uguXk.jpg")
				}
			},
			"<ac:image>\n  <ri:url ri:value=\"https://i.stack.imgur.com/uguXk.jpg\"></ri:url>\n</ac:image>\n",
			"Unexpected output")
	}

	@Test
	fun height() {
		Assert.assertEquals(
			buildString {
				appendHTML(true).acImage {
					acHeight = "300"
					riAttachment("028935e487e29a7c37fafdeec824946b112c71a0ff8781ff2ae6c9d909167c79.jpg")
				}
			},
			"<ac:image ac:height=\"300\">\n  <ri:attachment ri:filename=\"028935e487e29a7c37fafdeec824946b112c71a0ff8781ff2ae6c9d909167c79.jpg\"></ri:attachment>\n</ac:image>\n",
			"Unexpected output")
	}

	@Test
	fun width() {
		Assert.assertEquals(
			buildString {
				appendHTML(true).acImage {
					acWidth = "300"
					riAttachment("028935e487e29a7c37fafdeec824946b112c71a0ff8781ff2ae6c9d909167c79.jpg")
				}
			},
			"<ac:image ac:width=\"300\">\n  <ri:attachment ri:filename=\"028935e487e29a7c37fafdeec824946b112c71a0ff8781ff2ae6c9d909167c79.jpg\"></ri:attachment>\n</ac:image>\n",
			"Unexpected output")
	}

	@Test
	fun alt() {
		Assert.assertEquals(
			buildString {
				appendHTML(true).acImage {
					acAlt = "Alt girl"
					riAttachment("028935e487e29a7c37fafdeec824946b112c71a0ff8781ff2ae6c9d909167c79.jpg")
				}
			},
			"<ac:image ac:alt=\"Alt girl\">\n  <ri:attachment ri:filename=\"028935e487e29a7c37fafdeec824946b112c71a0ff8781ff2ae6c9d909167c79.jpg\"></ri:attachment>\n</ac:image>\n",
			"Unexpected output")
	}

	@Test
	fun title() {
		Assert.assertEquals(
			buildString {
				appendHTML(true).acImage {
					acTitle = "Title"
					riAttachment("028935e487e29a7c37fafdeec824946b112c71a0ff8781ff2ae6c9d909167c79.jpg")
				}
			},
			"<ac:image ac:title=\"Title\">\n  <ri:attachment ri:filename=\"028935e487e29a7c37fafdeec824946b112c71a0ff8781ff2ae6c9d909167c79.jpg\"></ri:attachment>\n</ac:image>\n",
			"Unexpected output")
	}
}
