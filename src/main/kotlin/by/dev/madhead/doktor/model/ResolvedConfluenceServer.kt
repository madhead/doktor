package by.dev.madhead.doktor.model

import java.io.Serializable

data class ResolvedConfluenceServer(
	val name: String,
	val url: String,
	val space: String,
	val user: String?,
	val password: String?
) : Serializable
