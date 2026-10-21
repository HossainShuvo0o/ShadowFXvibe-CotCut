package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persisted motion graphics project in Room database.
 */
@Entity(tableName = "motion_projects")
data class MotionProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "নতুন মোশন প্রজেক্ট",
    val text: String = "বাংলা মোশন স্টুডিও",
    val fontFamilyName: String = "Hind Siliguri",
    val fontSizeSp: Float = 36f,
    val isBold: Boolean = true,
    val isItalic: Boolean = false,
    val textAlign: String = "Center",
    val letterSpacingSp: Float = 1.0f,
    val textColorHex: String = "#00F0FF",
    val canvasBgColorHex: String = "#090B10",
    val hasTextBg: Boolean = false,
    val textBgColorHex: String = "#1E2433",
    val textBgCornerRadiusDp: Float = 12f,
    val textBgPaddingDp: Float = 12f,
    val hasStroke: Boolean = false,
    val strokeWidthDp: Float = 2.0f,
    val strokeColorHex: String = "#A855F7",
    val hasShadow: Boolean = true,
    val shadowColorHex: String = "#B3000000",
    val shadowBlurDp: Float = 10f,
    val shadowDxDp: Float = 2f,
    val shadowDyDp: Float = 4f,
    val opacity: Float = 1.0f,
    val animationType: String = "Bounce",
    val durationMs: Int = 1800,
    val delayMs: Int = 100,
    val speed: Float = 1.0f,
    val isLoop: Boolean = true,
    val aspectRatio: String = "9:16",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
