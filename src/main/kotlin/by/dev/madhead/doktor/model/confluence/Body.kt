package by.dev.madhead.doktor.model.confluence

data class Body(
	val storage: Storage
)

data class Storage(
	val value: String,
	val representation: String = "storage"
)
