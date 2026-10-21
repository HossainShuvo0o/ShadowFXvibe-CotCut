package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EditorState
import com.example.data.model.TypographyOptions
import com.example.domain.animation.AnimationEngine
import com.example.domain.animation.BengaliTextHelper
import com.example.domain.animation.MotionTransform
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.TextMuted

/**
 * Real-time motion graphics canvas preview supporting 9:16, 1:1, and 16:9 aspect ratios.
 */
@Composable
fun MotionCanvasPreview(
    state: EditorState,
    playbackProgress: Float,
    modifier: Modifier = Modifier
) {
    val aspectRatioFloat = when (state.aspectRatio) {
        "9:16" -> 9f / 16f
        "16:9" -> 16f / 9f
        else -> 1f
    }

    val transform: MotionTransform = remember(state.animationType, playbackProgress, state.text) {
        AnimationEngine.evaluate(state.animationType, playbackProgress, state.text)
    }

    val canvasBg = remember(state.canvasBgColorHex) {
        parseHexColor(state.canvasBgColorHex, Color(0xFF090B10))
    }
    val textColor = remember(state.textColorHex) {
        parseHexColor(state.textColorHex, Color.White)
    }
    val textBgColor = remember(state.textBgColorHex) {
        parseHexColor(state.textBgColorHex, Color(0xFF1E2433))
    }
    val strokeColor = remember(state.strokeColorHex) {
        parseHexColor(state.strokeColorHex, NeonCyan)
    }
    val shadowColor = remember(state.shadowColorHex) {
        parseHexColor(state.shadowColorHex, Color(0x99000000))
    }
    val fontFamily = remember(state.fontFamilyName) {
        TypographyOptions.getFontFamily(state.fontFamilyName)
    }
    val textAlign = remember(state.textAlign) {
        TypographyOptions.getTextAlign(state.textAlign)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .testTag("motion_canvas_container"),
        contentAlignment = Alignment.Center
    ) {
        val maxBoxHeight = maxHeight
        val maxBoxWidth = maxWidth

        // Canvas frame with rounded corners and subtle studio border
        Box(
            modifier = Modifier
                .aspectRatio(aspectRatioFloat, matchHeightConstraintsFirst = maxBoxHeight < 400.dp)
                .widthIn(max = 420.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(canvasBg)
                .border(1.dp, StudioBorder, RoundedCornerShape(16.dp))
                .testTag("motion_canvas_surface"),
            contentAlignment = Alignment.Center
        ) {
            // Render text with animated transforms
            AnimatedTextLayer(
                state = state,
                transform = transform,
                fontFamily = fontFamily,
                textAlign = textAlign,
                textColor = textColor,
                textBgColor = textBgColor,
                strokeColor = strokeColor,
                shadowColor = shadowColor
            )

            // Aspect ratio & resolution badge watermark in corner
            val resolutionLabel = when (state.aspectRatio) {
                "9:16" -> "9:16 • 1080×1920"
                "16:9" -> "16:9 • 1920×1080"
                else -> "1:1 • 1080×1080"
            }
            Text(
                text = resolutionLabel,
                color = TextMuted.copy(alpha = 0.5f),
                fontSize = 9.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AnimatedTextLayer(
    state: EditorState,
    transform: MotionTransform,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    textAlign: TextAlign,
    textColor: Color,
    textBgColor: Color,
    strokeColor: Color,
    shadowColor: Color
) {
    val textShadow = if (state.hasShadow) {
        Shadow(
            color = shadowColor,
            offset = Offset(state.shadowDxDp * 2f, state.shadowDyDp * 2f),
            blurRadius = state.shadowBlurDp * 2f
        )
    } else {
        Shadow.None
    }

    // Outer container applying animation transforms (Zoom, Bounce, Slide, Shake, Fade)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .graphicsLayer {
                scaleX = transform.scaleX
                scaleY = transform.scaleY
                translationX = transform.translationX
                translationY = transform.translationY
                rotationZ = transform.rotationZ
                alpha = transform.alpha * state.opacity
            },
        contentAlignment = Alignment.Center
    ) {
        // Optional Text Background badge
        val badgeModifier = if (state.hasTextBg) {
            Modifier
                .clip(RoundedCornerShape(state.textBgCornerRadiusDp.dp))
                .background(textBgColor)
                .padding(state.textBgPaddingDp.dp)
        } else {
            Modifier
        }

        Box(
            modifier = badgeModifier,
            contentAlignment = Alignment.Center
        ) {
            when (state.animationType) {
                "Glitch" -> {
                    // Render 3-layer chromatic aberration for Glitch
                    GlitchTextRender(
                        text = state.text,
                        state = state,
                        fontFamily = fontFamily,
                        textAlign = textAlign,
                        textColor = textColor,
                        strokeColor = strokeColor,
                        shadow = textShadow,
                        glitchOffsetX = transform.glitchOffsetX,
                        glitchOffsetY = transform.glitchOffsetY
                    )
                }

                "Typewriter" -> {
                    // Typewriter with blinking cursor
                    val displayText = transform.typewriterText + if ((transform.alpha * 10).toInt() % 2 == 0) "|" else " "
                    CoreTextRender(
                        text = displayText,
                        state = state,
                        fontFamily = fontFamily,
                        textAlign = textAlign,
                        textColor = textColor,
                        strokeColor = strokeColor,
                        shadow = textShadow
                    )
                }

                "WordByWord" -> {
                    // Word-by-word reveal / animated caption style
                    val words = remember(state.text) { BengaliTextHelper.getWords(state.text) }
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = when (state.textAlign) {
                            "Left" -> Arrangement.Start
                            "Right" -> Arrangement.End
                            else -> Arrangement.Center
                        },
                        verticalArrangement = Arrangement.Center
                    ) {
                        words.forEachIndexed { index, word ->
                            val isRevealed = index < transform.revealedWordsCount
                            val isActive = index == transform.activeWordIndex
                            val wordAlpha = if (isRevealed) 1.0f else 0.15f
                            val wordColor = if (isActive) NeonCyan else textColor

                            Text(
                                text = "$word ",
                                fontFamily = fontFamily,
                                fontSize = (state.fontSizeSp * if (isActive) 1.08f else 1.0f).sp,
                                fontWeight = if (state.isBold || isActive) FontWeight.Bold else FontWeight.Normal,
                                fontStyle = if (state.isItalic) FontStyle.Italic else FontStyle.Normal,
                                letterSpacing = state.letterSpacingSp.sp,
                                color = wordColor.copy(alpha = wordAlpha),
                                style = TextStyle(shadow = textShadow),
                                modifier = Modifier
                                    .graphicsLayer {
                                        if (isActive) {
                                            scaleX = 1.06f
                                            scaleY = 1.06f
                                        }
                                    }
                            )
                        }
                    }
                }

                else -> {
                    // Standard rendering for Fade, Slide, Zoom, Bounce, Shake
                    CoreTextRender(
                        text = state.text,
                        state = state,
                        fontFamily = fontFamily,
                        textAlign = textAlign,
                        textColor = textColor,
                        strokeColor = strokeColor,
                        shadow = textShadow
                    )
                }
            }
        }
    }
}

@Composable
private fun CoreTextRender(
    text: String,
    state: EditorState,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    textAlign: TextAlign,
    textColor: Color,
    strokeColor: Color,
    shadow: Shadow
) {
    Box(contentAlignment = Alignment.Center) {
        // If stroke is enabled, render stroke background layer
        if (state.hasStroke) {
            Text(
                text = text,
                fontFamily = fontFamily,
                fontSize = state.fontSizeSp.sp,
                fontWeight = if (state.isBold) FontWeight.Bold else FontWeight.Normal,
                fontStyle = if (state.isItalic) FontStyle.Italic else FontStyle.Normal,
                textAlign = textAlign,
                letterSpacing = state.letterSpacingSp.sp,
                color = strokeColor,
                style = TextStyle(
                    drawStyle = Stroke(width = state.strokeWidthDp * 3f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Foreground fill layer
        Text(
            text = text,
            fontFamily = fontFamily,
            fontSize = state.fontSizeSp.sp,
            fontWeight = if (state.isBold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (state.isItalic) FontStyle.Italic else FontStyle.Normal,
            textAlign = textAlign,
            letterSpacing = state.letterSpacingSp.sp,
            color = textColor,
            style = TextStyle(
                shadow = shadow,
                drawStyle = Fill
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun GlitchTextRender(
    text: String,
    state: EditorState,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    textAlign: TextAlign,
    textColor: Color,
    strokeColor: Color,
    shadow: Shadow,
    glitchOffsetX: Float,
    glitchOffsetY: Float
) {
    Box(contentAlignment = Alignment.Center) {
        // Red/Magenta chromatic offset channel
        Text(
            text = text,
            fontFamily = fontFamily,
            fontSize = state.fontSizeSp.sp,
            fontWeight = if (state.isBold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (state.isItalic) FontStyle.Italic else FontStyle.Normal,
            textAlign = textAlign,
            letterSpacing = state.letterSpacingSp.sp,
            color = Color(0xFFFF0055).copy(alpha = 0.75f),
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationX = -glitchOffsetX
                    translationY = -glitchOffsetY
                }
        )

        // Cyan chromatic offset channel
        Text(
            text = text,
            fontFamily = fontFamily,
            fontSize = state.fontSizeSp.sp,
            fontWeight = if (state.isBold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (state.isItalic) FontStyle.Italic else FontStyle.Normal,
            textAlign = textAlign,
            letterSpacing = state.letterSpacingSp.sp,
            color = Color(0xFF00F0FF).copy(alpha = 0.75f),
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationX = glitchOffsetX
                    translationY = glitchOffsetY
                }
        )

        // Main base text
        CoreTextRender(
            text = text,
            state = state,
            fontFamily = fontFamily,
            textAlign = textAlign,
            textColor = textColor,
            strokeColor = strokeColor,
            shadow = shadow
        )
    }
}

fun parseHexColor(hex: String, defaultColor: Color): Color {
    return try {
        val cleanHex = hex.removePrefix("#")
        when (cleanHex.length) {
            6 -> Color(android.graphics.Color.parseColor("#$cleanHex"))
            8 -> Color(android.graphics.Color.parseColor("#$cleanHex"))
            else -> defaultColor
        }
    } catch (_: Exception) {
        defaultColor
    }
}
