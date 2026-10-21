package com.shadowfxvibe.cotcut.util

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.R

object FontHelper {
    val availableFonts = listOf(
        "Poppins",
        "Montserrat",
        "Creepster",
        "Nosifer",
        "Shrikhand",
        "Anek Bangla",
        "Hind Siliguri",
        "System Sans"
    )

    fun getFontFamily(name: String): FontFamily {
        return runCatching {
            when (name) {
                "Poppins" -> FontFamily(Font(R.font.poppins, FontWeight.Normal))
                "Montserrat" -> FontFamily(Font(R.font.montserrat, FontWeight.Normal))
                "Creepster" -> FontFamily(Font(R.font.creepster, FontWeight.Normal))
                "Nosifer" -> FontFamily(Font(R.font.nosifer, FontWeight.Normal))
                "Shrikhand" -> FontFamily(Font(R.font.shrikhand, FontWeight.Normal))
                "Anek Bangla" -> FontFamily(Font(R.font.anek_bangla, FontWeight.Normal))
                "Hind Siliguri" -> FontFamily(Font(R.font.hind_siliguri, FontWeight.Normal))
                else -> FontFamily.SansSerif
            }
        }.getOrDefault(FontFamily.SansSerif)
    }
}
