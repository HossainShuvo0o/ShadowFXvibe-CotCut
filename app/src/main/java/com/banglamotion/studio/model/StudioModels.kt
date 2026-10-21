package com.banglamotion.studio.model

import androidx.compose.ui.graphics.Color
import com.banglamotion.studio.theme.*

enum class StudioScreen {
    EDITOR,
    TEMPLATES,
    PROJECTS,
    TOOLKIT,
    AUDIO_TRIM,
    AUDIO_MERGE,
    AUDIO_VOLUME,
    AUDIO_EXTRACT,
    AUDIO_CONVERT,
    AUDIO_RINGTONE,
    BG_REMOVER
}

enum class StudioSubTab {
    TOUCH_TEXT,
    ANIMATIONS,
    EFFECTS_AM,
    AUTO_CAPTION,
    MUSIC_SFX
}

data class MotionTemplateItem(
    val id: String,
    val title: String,
    val category: String, // "Popular", "FX", "Transitions", "Text", "Horror"
    val subtitle: String,
    val primaryText: String,
    val banglaText: String,
    val fontName: String,
    val animation: String,
    val effect: String,
    val badge: String = "PRO"
)

data class SubtitleWord(
    val word: String,
    val startMs: Long,
    val endMs: Long
)

data class EffectState(
    val name: String,
    val category: String, // 3D, Procedural, Blur, Matte, Glitch
    val isEnabled: Boolean = false,
    val intensity: Float = 0.5f,
    val description: String = ""
)

object StudioDefaultData {
    val quickBengaliPhrases = listOf(
        "মোশন ক্যাপশন",
        "ভয়াল অন্ধকার",
        "রক্তাক্ত অধ্যায়",
        "শেষ চিৎকার",
        "অদৃশ্য ছায়া",
        "মৃত্যুফাঁদ",
        "জীবনের গল্প",
        "নতুন দিগন্ত"
    )

    val fontOptions = listOf(
        "Nosifer Horror" to NosiferFont,
        "Creepster" to CreepsterFont,
        "Shrikhand" to ShrikhandFont,
        "Poppins" to PoppinsFont,
        "Anek Bangla" to AnekBanglaFont,
        "Hind Siliguri" to HindSiliguriFont,
        "Montserrat" to MontserratFont
    )

    val stillAnimations = listOf("Ken Burns", "Parallax", "Shake", "Rotate")
    val textAnimations = listOf("Pop", "Slide", "Zoom", "Bounce", "Typewriter", "Glitch", "Wave", "Neon")

    val templates = listOf(
        MotionTemplateItem(
            id = "t1",
            title = "Decoding Glitches",
            category = "FX",
            subtitle = "Purple Glitch & Chromatic Shift",
            primaryText = "YOU TEX",
            banglaText = "সাইবার ধ্বংস",
            fontName = "Creepster",
            animation = "Glitch",
            effect = "RGB Split"
        ),
        MotionTemplateItem(
            id = "t2",
            title = "Ripple Neon Glow",
            category = "Popular",
            subtitle = "Neon Distortion & Wave Warp",
            primaryText = "CYBER MONTAGE",
            banglaText = "নিয়ন তরঙ্গ",
            fontName = "Nosifer Horror",
            animation = "Wave",
            effect = "Wave Warp"
        ),
        MotionTemplateItem(
            id = "t3",
            title = "Retro TV Glitch",
            category = "FX",
            subtitle = "Cathode-ray Scanlines & Static",
            primaryText = "I SEE YOU",
            banglaText = "অন্ধকারের দৃষ্টি",
            fontName = "Shrikhand",
            animation = "Pop",
            effect = "Creepy Scanlines"
        ),
        MotionTemplateItem(
            id = "t4",
            title = "Outline Shake",
            category = "Text",
            subtitle = "Grunge Animated Titles 30fps",
            primaryText = "ANIMATED TITLES",
            banglaText = "কম্পিত শিরোনাম",
            fontName = "Poppins",
            animation = "Shake",
            effect = "Contour Strips"
        ),
        MotionTemplateItem(
            id = "t5",
            title = "Advanced Cinematic",
            category = "Popular",
            subtitle = "Horror 3D Extrude & Deep Glow",
            primaryText = "DO IT FOR ME",
            banglaText = "ভয়াল অধ্যায়",
            fontName = "Nosifer Horror",
            animation = "Bounce",
            effect = "Raster Extrude"
        ),
        MotionTemplateItem(
            id = "t6",
            title = "Illustrated Flow",
            category = "Transitions",
            subtitle = "Starlight Particle Swirl",
            primaryText = "ALL OF THEM",
            banglaText = "অনন্ত যাত্রা",
            fontName = "Anek Bangla",
            animation = "Typewriter",
            effect = "Bloom"
        )
    )

    val defaultEffects = listOf(
        EffectState("Raster Extrude", "3D", false, 0.6f, "Faux-3D raster extrusion block depth"),
        EffectState("Star Polyhedron", "3D", false, 0.5f, "Geometric 3D star perspective effect"),
        EffectState("Page Curl", "3D", false, 0.4f, "Corner page curl transition"),
        EffectState("Contour Strips", "Procedural", false, 0.7f, "Paper-like topographical depth strips"),
        EffectState("Streak Strips", "Blur", false, 0.5f, "Horizontal light streaks & motion smear"),
        EffectState("Motion Blur", "Blur", false, 0.6f, "Directional kinetic blur on movement"),
        EffectState("Luma Stamper", "Matte", false, 0.5f, "Brightness based dynamic matte masking"),
        EffectState("RGB Split", "Glitch", false, 0.7f, "Chromatic aberration red-cyan offset"),
        EffectState("Creepy Scanlines", "Glitch", false, 0.8f, "Retro CRT television scanline bars"),
        EffectState("Wave Warp", "Glitch", false, 0.5f, "Sinusoidal ripple wave distortion"),
        EffectState("Bloom", "Glitch", false, 0.6f, "Intense glowing light bleed on highlights")
    )
}
