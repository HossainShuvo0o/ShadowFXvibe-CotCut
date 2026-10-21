package com.example.domain.future

/**
 * Architectural abstractions and data contracts designed for future engine extensions.
 * These interfaces and models ensure future versions can seamlessly integrate multi-layer
 * video rendering, keyframing, masking, and audio synthesis without breaking database
 * or UI compatibility.
 */

// 1. Background Media Source (Video, Image, Gradient, Chroma Key)
sealed interface BackgroundMediaSource {
    data class SolidColor(val hex: String) : BackgroundMediaSource
    data class Gradient(val startHex: String, val endHex: String, val angleDeg: Float) : BackgroundMediaSource
    data class ImageAsset(val uri: String, val scaleType: String = "CenterCrop") : BackgroundMediaSource
    data class VideoTrack(
        val videoUri: String,
        val startTimeMs: Long = 0,
        val endTimeMs: Long = 0,
        val volume: Float = 1.0f,
        val isMuted: Boolean = false
    ) : BackgroundMediaSource
}

// 2. Timeline & Keyframe Animation System
data class KeyframePoint<T>(
    val timestampMs: Long,
    val value: T,
    val easingType: String = "Linear" // "EaseIn", "EaseOut", "Spring", "Bezier"
)

data class LayerTrack(
    val id: String,
    val name: String,
    val isVisible: Boolean = true,
    val isLocked: Boolean = false,
    val startMs: Long = 0,
    val durationMs: Long = 3000,
    val positionKeyframes: List<KeyframePoint<Pair<Float, Float>>> = emptyList(),
    val scaleKeyframes: List<KeyframePoint<Float>> = emptyList(),
    val opacityKeyframes: List<KeyframePoint<Float>> = emptyList(),
    val rotationKeyframes: List<KeyframePoint<Float>> = emptyList()
)

// 3. Masking Engine (Text, Shape, Image, Animated reveals)
enum class MaskShapeType {
    RECTANGLE,
    CIRCLE,
    ELLIPSE,
    LINEAR_WIPE,
    STAR,
    CUSTOM_PATH
}

data class MaskSpec(
    val id: String,
    val shapeType: MaskShapeType = MaskShapeType.RECTANGLE,
    val positionX: Float = 0.5f,
    val positionY: Float = 0.5f,
    val scaleX: Float = 1.0f,
    val scaleY: Float = 1.0f,
    val rotationDeg: Float = 0f,
    val featherPx: Float = 0f,
    val isInverted: Boolean = false,
    val isAnimatedReveal: Boolean = false,
    val revealProgress: Float = 1.0f
)

// 4. Background Removal & Matting
data class BackgroundMattingSpec(
    val isEnabled: Boolean = false,
    val chromaKeyColorHex: String? = null,
    val similarityThreshold: Float = 0.4f,
    val smoothness: Float = 0.1f,
    val aiSegmentationQuality: String = "Standard" // "Fast", "Standard", "High"
)

// 5. Audio Tracks & Video Export Engine
data class AudioTrackSpec(
    val id: String,
    val audioUri: String,
    val title: String,
    val volume: Float = 1.0f,
    val fadeInMs: Long = 0,
    val fadeOutMs: Long = 0,
    val startTimeOffsetMs: Long = 0
)

data class VideoRenderExportConfig(
    val outputWidth: Int = 1080,
    val outputHeight: Int = 1920,
    val fps: Int = 60,
    val bitrateKbps: Int = 12000,
    val format: String = "MP4_H264",
    val destinationPath: String = ""
)

// 6. Subtitles & Timed Caption Engine
data class CaptionWord(
    val text: String,
    val startMs: Long,
    val endMs: Long,
    val isHighlighted: Boolean = false
)

data class SubtitleSegment(
    val id: String,
    val fullSentence: String,
    val startMs: Long,
    val endMs: Long,
    val words: List<CaptionWord> = emptyList()
)
