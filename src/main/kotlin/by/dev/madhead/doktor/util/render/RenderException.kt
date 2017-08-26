package by.dev.madhead.doktor.util.render

class RenderException(override val message: String, override val cause: Throwable?) : Throwable(message, cause) {
	constructor(message: String) : this(message, null)
}
