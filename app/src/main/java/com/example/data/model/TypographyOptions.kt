package com.example.data.model

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.ui.theme.AnekBanglaFamily
import com.example.ui.theme.HindSiliguriFamily
import com.example.ui.theme.MontserratFamily

data class FontOption(
    val name: String,
    val displayName: String,
    val fontFamily: FontFamily,
    val isBanglaOptimized: Boolean = true
)

object TypographyOptions {
    val fonts = listOf(
        FontOption("Hind Siliguri", "হিন্দ শিলিগুড়ি (Hind Siliguri)", HindSiliguriFamily, true),
        FontOption("Anek Bangla", "অনেক বাংলা (Anek Bangla)", AnekBanglaFamily, true),
        FontOption("Montserrat", "Montserrat (Display)", MontserratFamily, false),
        FontOption("Sans Serif", "Sans-Serif (System)", FontFamily.SansSerif, false),
        FontOption("Serif", "Serif (System)", FontFamily.Serif, false),
        FontOption("Monospace", "Monospace (System)", FontFamily.Monospace, false),
        FontOption("Cursive", "Cursive (System)", FontFamily.Cursive, false)
    )

    fun getFontFamily(name: String): FontFamily {
        return fonts.find { it.name.equals(name, ignoreCase = true) }?.fontFamily ?: HindSiliguriFamily
    }

    val aspectRatios = listOf(
        "9:16" to "Reels / TikTok (9:16)",
        "1:1" to "Square Feed (1:1)",
        "16:9" to "Landscape (16:9)"
    )

    val animations = listOf(
        "Bounce" to "উচ্ছ্বাস (Bounce)",
        "Zoom" to "জুম ইন (Zoom)",
        "Fade" to "মিলিয়ে যাওয়া (Fade)",
        "Slide" to "স্লাইড (Slide)",
        "Shake" to "কম্পন (Shake)",
        "Typewriter" to "টাইপরাইটার (Typewriter)",
        "WordByWord" to "শব্দে শব্দে (Word by Word)",
        "Glitch" to "গ্লিচ ইফেক্ট (Glitch)"
    )

    val popularBanglaPhrases = listOf(
        "বাংলা মোশন স্টুডিও",
        "আমার সোনার বাংলা",
        "অমর একুশে",
        "বিজয় নিশান উড়ছে",
        "স্বপ্নের ঠিকানা",
        "শান্ত ভোর",
        "ব্রেকিং নিউজ",
        "শুভ জন্মদিন",
        "ভালোবাসার গল্প",
        "গতিশীল বাংলা"
    )

    val presetColors = listOf(
        "#00F0FF" to "Electric Cyan",
        "#A855F7" to "Vivid Violet",
        "#EC4899" to "Neon Pink",
        "#FFFFFF" to "Pure White",
        "#FFD700" to "Sunset Gold",
        "#22C55E" to "Neon Green",
        "#FB923C" to "Coral Orange",
        "#F43F5E" to "Rose Red",
        "#38BDF8" to "Sky Blue",
        "#E2E8F0" to "Slate Light",
        "#94A3B8" to "Cool Gray",
        "#000000" to "Deep Black"
    )

    val presetBackgroundColors = listOf(
        "#090B10" to "Pitch Charcoal",
        "#05070B" to "Obsidian Black",
        "#111827" to "Midnight Gray",
        "#0F172A" to "Dark Slate",
        "#1E1B4B" to "Deep Indigo",
        "#2E1065" to "Royal Violet",
        "#1E293B" to "Studio Slate",
        "#881337" to "Deep Crimson",
        "#14532D" to "Forest Green",
        "#FFFFFF" to "Clean White"
    )

    fun getTextAlign(align: String): TextAlign {
        return when (align) {
            "Left" -> TextAlign.Start
            "Right" -> TextAlign.End
            else -> TextAlign.Center
        }
    }
}
