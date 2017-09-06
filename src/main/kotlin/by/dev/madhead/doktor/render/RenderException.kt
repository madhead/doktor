package by.dev.madhead.doktor.render

class RenderException(message: String, cause: Throwable?) : Throwable(message, cause) {
	constructor(message: String) : this(message, null)
}
