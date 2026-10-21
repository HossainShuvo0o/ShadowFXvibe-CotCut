package com.example.domain.animation

import java.text.BreakIterator
import java.util.Locale

/**
 * Unicode-aware text helper that safely breaks Bengali and international text
 * into valid grapheme clusters and words without breaking conjuncts (যুক্তাক্ষর).
 */
object BengaliTextHelper {

    /**
     * Splits text into true grapheme clusters using BreakIterator.
     * In Bengali, conjuncts and vowel marks (কার) such as ক্ + ষ = ক্ষ
     * or ক + ো = কো must stay bound together.
     */
    fun getGraphemeClusters(text: String): List<String> {
        if (text.isEmpty()) return emptyList()
        val boundary = BreakIterator.getCharacterInstance(Locale("bn", "BD"))
        boundary.setText(text)
        val clusters = mutableListOf<String>()
        var start = boundary.first()
        var end = boundary.next()
        while (end != BreakIterator.DONE) {
            clusters.add(text.substring(start, end))
            start = end
            end = boundary.next()
        }
        return clusters
    }

    /**
     * Splits text into words while keeping whitespace intact for natural spacing.
     */
    fun getWords(text: String): List<String> {
        if (text.isBlank()) return listOf(text)
        return text.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    }

    /**
     * Returns progressive substring for typewriter effect up to progress (0f..1f).
     */
    fun getTypewriterSubstring(text: String, progress: Float): String {
        val clusters = getGraphemeClusters(text)
        if (clusters.isEmpty()) return ""
        val count = (clusters.size * progress.coerceIn(0f, 1f)).toInt().coerceIn(0, clusters.size)
        return clusters.take(count).joinToString("")
    }
}
