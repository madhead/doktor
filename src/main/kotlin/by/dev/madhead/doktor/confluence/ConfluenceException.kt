package by.dev.madhead.doktor.confluence

class ConfluenceException(override val message: String, cause: Throwable?) : Throwable(message, cause) {
	constructor(message: String) : this(message, null)
}
