package com.shadowfxvibe.cotcut.model

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color

enum class TransitionType(val displayName: String, val description: String) {
    Pop("Pop", "Punchy bounce in"),
    ZoomIn("Zoom In", "Smooth scale up from center"),
    ZoomOut("Zoom Out", "Scale down impact"),
    Scale("Scale", "Dynamic pulse scaling"),
    SlideDown("Slide Down", "Drop down from top"),
    SlideUp("Slide Up", "Ascend from bottom"),
    FadeIn("Fade In", "Cinematic smooth opacity")
}

enum class BlendModeType(val displayName: String) {
    Normal("Normal"),
    Screen("Screen"),
    Overlay("Overlay");

    fun toComposeBlendMode(): BlendMode = when (this) {
        Normal -> BlendMode.SrcOver
        Screen -> BlendMode.Screen
        Overlay -> BlendMode.Overlay
    }
}

enum class MaskShapeType(val displayName: String) {
    None("Off"),
    Linear("Linear"),
    Circle("Circle"),
    Rectangle("Rectangle")
}

enum class BreakType(val displayName: String) {
    WordByWord("Word by Word"),
    Phrase("Phrase (2-3 words)"),
    LineBreak("Line Break"),
    Sentence("Full Sentence"),
    Karaoke("Karaoke Highlight")
}

data class Keyframe(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timeMs: Long,
    val x: Float,
    val y: Float,
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val alpha: Float = 1.0f
)

data class CaptionItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val x: Float = 0f, // X offset in pixels relative to center
    val y: Float = 0f, // Y offset in pixels relative to center
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val color: Long = 0xFF00E5FF, // Neon cyan default
    val strokeColor: Long = 0xFF000000,
    val fontFamilyName: String = "Poppins",
    val fontSize: Float = 28f,
    val isBold: Boolean = true,
    val transition: TransitionType = TransitionType.Pop,
    val hasTransition: Boolean = true,
    val randomizeRotation: Boolean = false,
    val randomizeMovement: Boolean = false,
    val blendMode: BlendModeType = BlendModeType.Normal,
    val maskShape: MaskShapeType = MaskShapeType.None,
    val maskIntensity: Float = 0.5f,
    val breakType: BreakType = BreakType.WordByWord,
    val startTimeMs: Long = 0L,
    val endTimeMs: Long = 5000L,
    val keyframes: List<Keyframe> = emptyList()
)

data class TrendingStyle(
    val id: String,
    val category: String,
    val title: String,
    val previewText: String,
    val fontName: String,
    val color: Long,
    val strokeColor: Long = 0xFF000000,
    val transition: TransitionType,
    val blendMode: BlendModeType = BlendModeType.Normal,
    val tag: String = "HOT"
)

enum class ResolutionOption(val label: String, val description: String, val width: Int, val height: Int) {
    HD_1080P("1080p Full HD", "1080 x 1920 (TikTok & Reels standard)", 1080, 1920),
    UHD_4K("4K Ultra HD", "2160 x 3840 (Maximum crisp quality)", 2160, 3840)
}

enum class FrameRateOption(val fps: Int, val label: String) {
    FPS_24(24, "24 fps (Cinematic film)"),
    FPS_30(30, "30 fps (Standard social)"),
    FPS_60(60, "60 fps (Ultra smooth)")
}

data class ExportConfig(
    val resolution: ResolutionOption = ResolutionOption.HD_1080P,
    val frameRate: FrameRateOption = FrameRateOption.FPS_30,
    val bitrateMbps: Int = 18,
    val includeAudio: Boolean = true
)

enum class TrackType {
    Video,
    Audio,
    Text
}

data class TimelineTrack(
    val type: TrackType,
    val title: String,
    val durationMs: Long
)
