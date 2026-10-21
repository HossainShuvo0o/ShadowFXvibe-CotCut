package com.banglamotion.studio.ui

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.banglamotion.studio.model.EffectState
import com.banglamotion.studio.theme.*
import kotlin.math.*

@Composable
fun StudioCanvas(
    mainText: String,
    banglaText: String,
    fontFamily: FontFamily,
    fontSizeSp: Float,
    isBold: Boolean,
    isItalic: Boolean,
    hasOuterGlow: Boolean,
    hasStroke: Boolean,
    has3DExtrude: Boolean,
    stillAnimation: String, // "Ken Burns", "Parallax", "Shake", "Rotate"
    textAnimation: String,  // "Pop", "Slide", "Zoom", "Bounce", "Typewriter", "Glitch", "Wave", "Neon"
    effectsList: List<EffectState>,
    playbackProgress: Float, // 0f to 1f (from timeline)
    backgroundImageUri: Uri?,
    posX: Float,
    posY: Float,
    scale: Float,
    rotation: Float,
    onTransformChange: (x: Float, y: Float, scale: Float, rot: Float) -> Unit,
    onCenterClick: () -> Unit,
    onGroupClick: () -> Unit,
    isGrouped: Boolean,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    // Calculate animation factors based on textAnimation & playbackProgress
    val animScaleFactor = when (textAnimation) {
        "Bounce" -> 1f + sin(playbackProgress * PI.toFloat() * 4f) * 0.12f
        "Zoom" -> 0.7f + 0.3f * playbackProgress
        "Pop" -> if (playbackProgress < 0.2f) (playbackProgress / 0.2f) * 1.15f else 1f
        "Neon" -> 1f + sin(playbackProgress * 12f) * 0.05f
        else -> 1f
    }

    val animAlphaFactor = when (textAnimation) {
        "Typewriter" -> 1f
        "Glitch" -> if ((playbackProgress * 20).toInt() % 3 == 0) 0.85f else 1f
        else -> 1f
    }

    val animOffsetX = when (textAnimation) {
        "Slide" -> (1f - (playbackProgress * 3f).coerceAtMost(1f)) * -120f
        "Wave" -> sin(playbackProgress * PI.toFloat() * 6f) * 15f
        "Glitch" -> if ((playbackProgress * 25).toInt() % 4 == 0) 8f else 0f
        else -> 0f
    }

    // Typewriter text slicing
    val displayedMainText = if (textAnimation == "Typewriter") {
        val totalChars = mainText.length
        val visibleCount = ((playbackProgress * 1.5f).coerceAtMost(1f) * totalChars).toInt()
        mainText.take(visibleCount)
    } else {
        mainText
    }

    // Effects flags
    val hasScanlines = effectsList.find { it.name == "Creepy Scanlines" }?.isEnabled == true
    val hasRgbSplit = effectsList.find { it.name == "RGB Split" }?.isEnabled == true
    val hasWaveWarp = effectsList.find { it.name == "Wave Warp" }?.isEnabled == true
    val hasContourStrips = effectsList.find { it.name == "Contour Strips" }?.isEnabled == true
    val extrudeEffect = effectsList.find { it.name == "Raster Extrude" }
    val isExtrudeActive = has3DExtrude || (extrudeEffect?.isEnabled == true)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 9:16 Viewport Box
        Box(
            modifier = Modifier
                .widthIn(max = 290.dp)
                .aspectRatio(9f / 16f)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF050505))
                .border(2.dp, CardBorderGradient, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Background Image / Ken Burns Layer
            if (backgroundImageUri != null) {
                val kbScale = if (stillAnimation == "Ken Burns") 1.0f + (playbackProgress * 0.25f) else 1.0f
                val kbShiftX = if (stillAnimation == "Parallax") sin(playbackProgress * PI.toFloat()) * 30f else 0f
                AsyncImage(
                    model = backgroundImageUri,
                    contentDescription = "Canvas Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = kbScale
                            scaleY = kbScale
                            translationX = kbShiftX
                        }
                )
            } else {
                // Procedural dark smoke / horror background gradient
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Radial dark glow
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF260505), Color(0xFF050202), Color(0xFF000000)),
                            center = Offset(w * 0.5f, h * 0.45f),
                            radius = w * 0.85f
                        )
                    )

                    // Contour strips procedural effect
                    if (hasContourStrips) {
                        for (i in 0..12) {
                            val yOffset = (h / 12f) * i + sin(playbackProgress * 3f + i) * 10f
                            drawLine(
                                color = RedPrimary.copy(alpha = 0.15f),
                                start = Offset(0f, yOffset),
                                end = Offset(w, yOffset + 25f),
                                strokeWidth = 2.5f
                            )
                        }
                    }
                }
            }

            // Foreground Interactive Canvas (Text + Subtitle + Effects)
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, rotate ->
                            val newX = posX + pan.x
                            val newY = posY + pan.y
                            val newScale = (scale * zoom).coerceIn(0.4f, 3.5f)
                            val newRot = (rotation + rotate)
                            onTransformChange(newX, newY, newScale, newRot)
                        }
                    }
            ) {
                val w = size.width
                val h = size.height

                // Draw background particles / effects within viewport
                val centerX = w / 2f + posX + animOffsetX
                val centerY = h * 0.42f + posY
                val finalScale = scale * animScaleFactor

                // 1. Render Primary Text (Multi-Layer Canvas Rendering)
                if (displayedMainText.isNotEmpty()) {
                    val textStyle = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = (fontSizeSp * finalScale).sp,
                        fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Bold,
                        color = TextWhite,
                        textAlign = TextAlign.Center
                    )

                    val textLayout = textMeasurer.measure(
                        text = AnnotatedString(displayedMainText),
                        style = textStyle
                    )

                    val textWidth = textLayout.size.width.toFloat()
                    val textHeight = textLayout.size.height.toFloat()
                    val textOrigin = Offset(centerX - textWidth / 2f, centerY - textHeight / 2f)

                    // Transform Canvas for Rotation
                    drawContext.canvas.save()
                    drawContext.canvas.rotate(rotation, centerX, centerY)

                    // Layer 0: 3D Raster Extrude (Stacked block shadow)
                    if (isExtrudeActive) {
                        val extrudeDepth = 10
                        for (i in extrudeDepth downTo 1) {
                            val stepOffset = Offset(i * 1.8f, i * 2.2f)
                            drawText(
                                textMeasurer = textMeasurer,
                                text = displayedMainText,
                                topLeft = textOrigin + stepOffset,
                                style = textStyle.copy(
                                    color = Color(0xFF380325).copy(alpha = 0.85f)
                                )
                            )
                        }
                    }

                    // Layer 1: Red Outer Glow Blur (#FF1A1A)
                    if (hasOuterGlow) {
                        for (radius in listOf(14f, 8f, 4f)) {
                            drawText(
                                textMeasurer = textMeasurer,
                                text = displayedMainText,
                                topLeft = textOrigin,
                                style = textStyle.copy(
                                    color = RedPrimary.copy(alpha = 0.45f)
                                )
                            )
                        }
                    }

                    // Layer 2: RGB Split Glitch (Chromatic aberration)
                    if (hasRgbSplit) {
                        drawText(
                            textMeasurer = textMeasurer,
                            text = displayedMainText,
                            topLeft = textOrigin + Offset(-6f, 0f),
                            style = textStyle.copy(color = Color(0xFF00E5FF).copy(alpha = 0.65f))
                        )
                        drawText(
                            textMeasurer = textMeasurer,
                            text = displayedMainText,
                            topLeft = textOrigin + Offset(6f, 0f),
                            style = textStyle.copy(color = Color(0xFFFF0055).copy(alpha = 0.65f))
                        )
                    }

                    // Layer 3: Black Stroke Outline (6px equivalent)
                    if (hasStroke) {
                        // Multi-pass stroke simulation for crisp text contrast
                        for (dx in listOf(-4f, 0f, 4f)) {
                            for (dy in listOf(-4f, 0f, 4f)) {
                                if (dx != 0f || dy != 0f) {
                                    drawText(
                                        textMeasurer = textMeasurer,
                                        text = displayedMainText,
                                        topLeft = textOrigin + Offset(dx, dy),
                                        style = textStyle.copy(color = Color.Black)
                                    )
                                }
                            }
                        }
                    }

                    // Layer 4: Red to Orange to Yellow Gradient Fill
                    drawText(
                        textMeasurer = textMeasurer,
                        text = displayedMainText,
                        topLeft = textOrigin,
                        style = textStyle.copy(
                            brush = Brush.linearGradient(
                                colors = listOf(RedPrimary, OrangePrimary, OrangeYellow),
                                start = Offset(0f, 0f),
                                end = Offset(textWidth, textHeight)
                            )
                        )
                    )

                    // Dashed Purple / Red-Orange Selection Box + Corner Handles
                    val boxPadding = 12f
                    val boxRect = Rect(
                        textOrigin.x - boxPadding,
                        textOrigin.y - boxPadding,
                        textOrigin.x + textWidth + boxPadding,
                        textOrigin.y + textHeight + boxPadding
                    )

                    drawRoundRect(
                        brush = Brush.horizontalGradient(listOf(Color(0xFFA855F7), RedPrimary, OrangePrimary)),
                        topLeft = Offset(boxRect.left, boxRect.top),
                        size = Size(boxRect.width, boxRect.height),
                        cornerRadius = CornerRadius(8f, 8f),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                        )
                    )

                    // 4 Corner resize handles
                    val handleRadius = 5.dp.toPx()
                    listOf(
                        Offset(boxRect.left, boxRect.top),
                        Offset(boxRect.right, boxRect.top),
                        Offset(boxRect.left, boxRect.bottom),
                        Offset(boxRect.right, boxRect.bottom)
                    ).forEach { pt ->
                        drawCircle(color = TextWhite, radius = handleRadius, center = pt)
                        drawCircle(color = OrangePrimary, radius = handleRadius - 2f, center = pt)
                    }

                    drawContext.canvas.restore()
                }

                // 2. Render Bangla Subtitle Box at bottom
                if (banglaText.isNotEmpty()) {
                    val subStyle = TextStyle(
                        fontFamily = AnekBanglaFont,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BanglaOrange,
                        textAlign = TextAlign.Center
                    )

                    val subLayout = textMeasurer.measure(
                        text = AnnotatedString(banglaText),
                        style = subStyle
                    )

                    val subW = subLayout.size.width.toFloat() + 32f
                    val subH = subLayout.size.height.toFloat() + 14f
                    val subX = w / 2f - subW / 2f
                    val subY = h * 0.82f

                    // Subtitle container card
                    drawRoundRect(
                        color = Color(0xFF140707).copy(alpha = 0.92f),
                        topLeft = Offset(subX, subY),
                        size = Size(subW, subH),
                        cornerRadius = CornerRadius(14f, 14f)
                    )
                    drawRoundRect(
                        brush = Brush.horizontalGradient(listOf(RedPrimary, OrangePrimary)),
                        topLeft = Offset(subX, subY),
                        size = Size(subW, subH),
                        cornerRadius = CornerRadius(14f, 14f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    drawText(
                        textMeasurer = textMeasurer,
                        text = banglaText,
                        topLeft = Offset(w / 2f - subLayout.size.width / 2f, subY + 7f),
                        style = subStyle
                    )
                }

                // CRT Creepy Scanlines Overlay
                if (hasScanlines) {
                    val lineSpacing = 6f
                    var curY = 0f
                    while (curY < h) {
                        drawLine(
                            color = Color.Black.copy(alpha = 0.35f),
                            start = Offset(0f, curY),
                            end = Offset(w, curY),
                            strokeWidth = 2f
                        )
                        curY += lineSpacing
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Controlpad Coordinates & Quick Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Controlpad: X: ${"%.1f".format(posX)} | Y: ${"%.1f".format(posY)} | Scale: ${"%.2f".format(scale)}x | Rot: ${rotation.toInt()}°",
                color = OrangeYellow,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Center Button
                OutlinedButton(
                    onClick = onCenterClick,
                    modifier = Modifier.height(28.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                    border = BorderStroke(1.dp, StudioCardBorder)
                ) {
                    Icon(Icons.Default.CenterFocusStrong, contentDescription = "Center", tint = OrangePrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Center", fontSize = 11.sp)
                }

                // Group Button
                OutlinedButton(
                    onClick = onGroupClick,
                    modifier = Modifier.height(28.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isGrouped) GreenFAB else TextWhite
                    ),
                    border = BorderStroke(1.dp, if (isGrouped) GreenFAB else StudioCardBorder)
                ) {
                    Icon(Icons.Default.Layers, contentDescription = "Group", tint = if (isGrouped) GreenFAB else OrangePrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isGrouped) "Grouped" else "Group", fontSize = 11.sp)
                }
            }
        }
    }
}
