package com.example.domain.animation

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

data class MotionTransform(
    val alpha: Float = 1.0f,
    val scaleX: Float = 1.0f,
    val scaleY: Float = 1.0f,
    val translationX: Float = 0f,
    val translationY: Float = 0f,
    val rotationZ: Float = 0f,
    val glitchOffsetX: Float = 0f,
    val glitchOffsetY: Float = 0f,
    val glitchActive: Boolean = false,
    val typewriterText: String = "",
    val activeWordIndex: Int = -1,
    val revealedWordsCount: Int = 0
)

object AnimationEngine {

    /**
     * Calculates the motion transform for the given animation type and normalized time (0f to 1f).
     */
    fun evaluate(
        animationType: String,
        progress: Float,
        rawText: String
    ): MotionTransform {
        val t = progress.coerceIn(0f, 1f)

        return when (animationType) {
            "Fade" -> {
                // Smooth ease in and ease out breathing fade
                val alpha = if (t < 0.2f) {
                    t / 0.2f
                } else if (t > 0.85f) {
                    (1f - t) / 0.15f
                } else {
                    1f
                }
                MotionTransform(alpha = alpha.coerceIn(0f, 1f))
            }

            "Slide" -> {
                // Smooth deceleration entry from bottom/left
                val entryT = (t / 0.35f).coerceIn(0f, 1f)
                val easeOutCubic = 1f - (1f - entryT) * (1f - entryT) * (1f - entryT)
                val translateY = (1f - easeOutCubic) * 120f
                val alpha = (entryT * 1.5f).coerceIn(0f, 1f)
                MotionTransform(
                    alpha = alpha,
                    translationY = translateY
                )
            }

            "Zoom" -> {
                // Dramatic zoom with subtle spring overshoot
                val entryT = (t / 0.4f).coerceIn(0f, 1f)
                val scale = if (entryT < 0.8f) {
                    val p = entryT / 0.8f
                    0.2f + 0.95f * p
                } else {
                    val p = (entryT - 0.8f) / 0.2f
                    1.15f - 0.15f * p
                }
                val alpha = (entryT * 2f).coerceIn(0f, 1f)
                MotionTransform(
                    alpha = alpha,
                    scaleX = scale,
                    scaleY = scale
                )
            }

            "Bounce" -> {
                // Tactile elastic spring bounce
                val entryT = (t / 0.6f).coerceIn(0f, 1f)
                val bounce = if (entryT >= 1f) 1f else {
                    1f + (sin(entryT * PI * 3.5).toFloat() * exp(-entryT * 4.5f) * 0.4f)
                }
                val alpha = (entryT * 3f).coerceIn(0f, 1f)
                MotionTransform(
                    alpha = alpha,
                    scaleX = bounce.coerceIn(0.1f, 1.6f),
                    scaleY = bounce.coerceIn(0.1f, 1.6f)
                )
            }

            "Shake" -> {
                // Kinetic rumble/vibration with exponential decay
                val cycleT = (t % 0.5f) / 0.5f
                val decay = (1f - cycleT).coerceIn(0f, 1f)
                val shakeX = sin(cycleT * PI.toFloat() * 14f) * 16f * decay
                val shakeY = cos(cycleT * PI.toFloat() * 12f) * 8f * decay
                val rotZ = sin(cycleT * PI.toFloat() * 10f) * 4f * decay
                MotionTransform(
                    alpha = 1.0f,
                    translationX = shakeX,
                    translationY = shakeY,
                    rotationZ = rotZ
                )
            }

            "Typewriter" -> {
                // Progressive reveal of Bengali grapheme clusters
                val textRevealT = (t / 0.75f).coerceIn(0f, 1f)
                val substring = BengaliTextHelper.getTypewriterSubstring(rawText, textRevealT)
                MotionTransform(
                    alpha = 1.0f,
                    typewriterText = substring
                )
            }

            "WordByWord" -> {
                // Word by word progressive highlight & reveal
                val words = BengaliTextHelper.getWords(rawText)
                val totalWords = words.size.coerceAtLeast(1)
                val activeIndex = ((t / 0.85f).coerceIn(0f, 0.999f) * totalWords).toInt()
                val revealedCount = (activeIndex + 1).coerceAtMost(totalWords)
                MotionTransform(
                    alpha = 1.0f,
                    activeWordIndex = activeIndex,
                    revealedWordsCount = revealedCount
                )
            }

            "Glitch" -> {
                // Chromatic split & jitter pulses
                val isFlickerTime = (t * 20f).toInt() % 4 == 0
                val jitterX = if (isFlickerTime) {
                    sin(t * 80f) * 14f
                } else 0f
                val jitterY = if (isFlickerTime) {
                    cos(t * 60f) * 4f
                } else 0f

                MotionTransform(
                    alpha = if (isFlickerTime && (t * 40f).toInt() % 3 == 0) 0.85f else 1.0f,
                    translationX = jitterX,
                    translationY = jitterY,
                    glitchOffsetX = if (isFlickerTime) 12f else 4f,
                    glitchOffsetY = if (isFlickerTime) -6f else 2f,
                    glitchActive = true
                )
            }

            else -> MotionTransform()
        }
    }
}
