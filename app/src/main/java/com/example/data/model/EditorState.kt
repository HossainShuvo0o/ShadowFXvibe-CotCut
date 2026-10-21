package com.example.data.model

/**
 * In-memory editor state snapshot used for live preview and Undo/Redo history.
 */
data class EditorState(
    val projectId: Long = 0,
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
    val aspectRatio: String = "9:16"
) {
    fun toMotionProject(createdAt: Long = System.currentTimeMillis()): MotionProject {
        return MotionProject(
            id = projectId,
            title = title,
            text = text,
            fontFamilyName = fontFamilyName,
            fontSizeSp = fontSizeSp,
            isBold = isBold,
            isItalic = isItalic,
            textAlign = textAlign,
            letterSpacingSp = letterSpacingSp,
            textColorHex = textColorHex,
            canvasBgColorHex = canvasBgColorHex,
            hasTextBg = hasTextBg,
            textBgColorHex = textBgColorHex,
            textBgCornerRadiusDp = textBgCornerRadiusDp,
            textBgPaddingDp = textBgPaddingDp,
            hasStroke = hasStroke,
            strokeWidthDp = strokeWidthDp,
            strokeColorHex = strokeColorHex,
            hasShadow = hasShadow,
            shadowColorHex = shadowColorHex,
            shadowBlurDp = shadowBlurDp,
            shadowDxDp = shadowDxDp,
            shadowDyDp = shadowDyDp,
            opacity = opacity,
            animationType = animationType,
            durationMs = durationMs,
            delayMs = delayMs,
            speed = speed,
            isLoop = isLoop,
            aspectRatio = aspectRatio,
            createdAt = createdAt,
            updatedAt = System.currentTimeMillis()
        )
    }

    companion object {
        fun fromMotionProject(project: MotionProject): EditorState {
            return EditorState(
                projectId = project.id,
                title = project.title,
                text = project.text,
                fontFamilyName = project.fontFamilyName,
                fontSizeSp = project.fontSizeSp,
                isBold = project.isBold,
                isItalic = project.isItalic,
                textAlign = project.textAlign,
                letterSpacingSp = project.letterSpacingSp,
                textColorHex = project.textColorHex,
                canvasBgColorHex = project.canvasBgColorHex,
                hasTextBg = project.hasTextBg,
                textBgColorHex = project.textBgColorHex,
                textBgCornerRadiusDp = project.textBgCornerRadiusDp,
                textBgPaddingDp = project.textBgPaddingDp,
                hasStroke = project.hasStroke,
                strokeWidthDp = project.strokeWidthDp,
                strokeColorHex = project.strokeColorHex,
                hasShadow = project.hasShadow,
                shadowColorHex = project.shadowColorHex,
                shadowBlurDp = project.shadowBlurDp,
                shadowDxDp = project.shadowDxDp,
                shadowDyDp = project.shadowDyDp,
                opacity = project.opacity,
                animationType = project.animationType,
                durationMs = project.durationMs,
                delayMs = project.delayMs,
                speed = project.speed,
                isLoop = project.isLoop,
                aspectRatio = project.aspectRatio
            )
        }
    }
}
