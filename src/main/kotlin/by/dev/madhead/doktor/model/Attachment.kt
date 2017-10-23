package by.dev.madhead.doktor.model

import java.io.Serializable
import java.util.Arrays

data class Attachment(
	val fileName: String,
	val bytes: ByteArray
) : Serializable {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Attachment

		if (fileName != other.fileName) return false
		if (!Arrays.equals(bytes, other.bytes)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = fileName.hashCode()

		result = 31 * result + Arrays.hashCode(bytes)
		return result
	}
}
