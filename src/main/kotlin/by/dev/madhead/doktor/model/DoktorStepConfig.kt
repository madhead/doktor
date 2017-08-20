package by.dev.madhead.doktor.model

import java.io.Serializable

typealias MarkupPatterns = Map<Markup, Pair<List<String>, List<String>>>

data class DoktorStepConfig(
	val server: String,
	val markupPatterns: MarkupPatterns
) : Serializable
