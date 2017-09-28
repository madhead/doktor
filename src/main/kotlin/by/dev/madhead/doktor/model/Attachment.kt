package by.dev.madhead.doktor.model

import java.io.Serializable

data class Attachment(
	val fileName: String,
	val bytes: ByteArray
) : Serializable
