package com.shadowfxvibe.cotcut.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shadowfxvibe.cotcut.model.*
import com.shadowfxvibe.cotcut.ui.theme.*
import com.shadowfxvibe.cotcut.util.FontHelper

enum class ControlTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    ColorTab("Color", Icons.Default.Palette),
    FontTab("Font", Icons.Default.TextFields),
    BreaksTab("Breaks", Icons.Default.WrapText),
    TransitionsTab("Transitions", Icons.Default.AutoAwesome),
    MaskTab("Mask & Blend", Icons.Default.Layers),
    KeyframeTab("Keyframes", Icons.Default.Timeline)
}

@Composable
fun ControlPanel(
    selectedCaption: CaptionItem?,
    currentPlayheadMs: Long,
    onUpdateCaption: (CaptionItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedCaption == null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(CardDark)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tap on a text caption on canvas or timeline to edit",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
        return
    }

    var selectedTab by remember { mutableStateOf(ControlTab.ColorTab) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CardDark)
    ) {
        // Text Input Bar for quick editing
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = selectedCaption.text,
                onValueChange = { newText ->
                    onUpdateCaption(selectedCaption.copy(text = newText))
                },
                placeholder = { Text("Enter caption text...", color = TextSecondary) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = CardDarkElevated,
                    unfocusedContainerColor = CardDarkElevated
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("caption_text_input")
            )
        }

        // Horizontal Category Tab Bar
        ScrollableTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = DeepBlack,
            contentColor = NeonCyan,
            edgePadding = 12.dp,
            divider = { HorizontalDivider(color = BorderSubtle, thickness = 1.dp) }
        ) {
            ControlTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Tab(
                    selected = isSelected,
                    onClick = { selectedTab = tab },
                    text = {
                        Text(
                            text = tab.label,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) NeonCyan else TextSecondary,
                            fontSize = 13.sp
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = if (isSelected) NeonCyan else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.testTag("tab_${tab.name}")
                )
            }
        }

        // Tab Content Panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 180.dp, max = 220.dp)
                .background(CardDark)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            when (selectedTab) {
                ControlTab.ColorTab -> ColorControlSection(selectedCaption, onUpdateCaption)
                ControlTab.FontTab -> FontControlSection(selectedCaption, onUpdateCaption)
                ControlTab.BreaksTab -> BreaksControlSection(selectedCaption, onUpdateCaption)
                ControlTab.TransitionsTab -> TransitionsControlSection(selectedCaption, onUpdateCaption)
                ControlTab.MaskTab -> MaskControlSection(selectedCaption, onUpdateCaption)
                ControlTab.KeyframeTab -> KeyframeControlSection(selectedCaption, currentPlayheadMs, onUpdateCaption)
            }
        }
    }
}

@Composable
private fun ColorControlSection(
    caption: CaptionItem,
    onUpdateCaption: (CaptionItem) -> Unit
) {
    val colorPalette = listOf(
        0xFF00E5FF to "Neon Cyan",
        0xFFFF007F to "Neon Pink",
        0xFFFFD600 to "Electric Yellow",
        0xFF00E676 to "Neon Green",
        0xFFA855F7 to "Electric Purple",
        0xFFFFFFFF to "Pure White",
        0xFFFF5722 to "Flame Orange",
        0xFF0F0F0F to "Midnight Black"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Text Color", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            colorPalette.forEach { (colorHex, name) ->
                val isSelected = caption.color == colorHex
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(colorHex))
                        .border(
                            2.dp,
                            if (isSelected) NeonCyan else Color(0xFF404040),
                            CircleShape
                        )
                        .clickable { onUpdateCaption(caption.copy(color = colorHex)) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = name,
                            tint = if (colorHex == 0xFFFFFFFFL || colorHex == 0xFFFFD600L || colorHex == 0xFF00E5FFL) DeepBlack else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Font Size Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Font Size: ${caption.fontSize.toInt()}sp", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Bold", style = MaterialTheme.typography.labelSmall, color = if (caption.isBold) NeonCyan else TextSecondary)
                Switch(
                    checked = caption.isBold,
                    onCheckedChange = { onUpdateCaption(caption.copy(isBold = it)) },
                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = Color(0xFF004D59))
                )
            }
        }
        Slider(
            value = caption.fontSize,
            onValueChange = { onUpdateCaption(caption.copy(fontSize = it)) },
            valueRange = 18f..64f,
            colors = SliderDefaults.colors(
                thumbColor = NeonCyan,
                activeTrackColor = NeonCyan,
                inactiveTrackColor = CardDarkElevated
            )
        )
    }
}

@Composable
private fun FontControlSection(
    caption: CaptionItem,
    onUpdateCaption: (CaptionItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Select Kinetic Typography Font", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FontHelper.availableFonts.forEach { fontName ->
                val isSelected = caption.fontFamilyName == fontName
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) NeonCyan.copy(alpha = 0.18f) else CardDarkElevated)
                        .border(1.dp, if (isSelected) NeonCyan else BorderSubtle, RoundedCornerShape(12.dp))
                        .clickable { onUpdateCaption(caption.copy(fontFamilyName = fontName)) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Shadow FX",
                            fontFamily = FontHelper.getFontFamily(fontName),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) NeonCyan else TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = fontName,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BreaksControlSection(
    caption: CaptionItem,
    onUpdateCaption: (CaptionItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Caption Segmentation & Breaks", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))

        BreakType.entries.forEach { breakType ->
            val isSelected = caption.breakType == breakType
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) NeonCyan.copy(alpha = 0.15f) else CardDarkElevated)
                    .border(1.dp, if (isSelected) NeonCyan else Color.Transparent, RoundedCornerShape(10.dp))
                    .clickable { onUpdateCaption(caption.copy(breakType = breakType)) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = breakType.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) NeonCyan else TextPrimary
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransitionsControlSection(
    caption: CaptionItem,
    onUpdateCaption: (CaptionItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Toggle 1: Transitions Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Transitions",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Animate caption entry & dynamics",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Switch(
                checked = caption.hasTransition,
                onCheckedChange = { onUpdateCaption(caption.copy(hasTransition = it)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NeonCyan,
                    checkedTrackColor = Color(0xFF004D59)
                ),
                modifier = Modifier.testTag("toggle_transitions")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Transition Effects Horizontal List
        AnimatedVisibility(visible = caption.hasTransition) {
            Column {
                Text(
                    text = "Transition Effects",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TransitionType.entries.forEach { effect ->
                        val isSelected = caption.transition == effect
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) NeonCyan else CardDarkElevated)
                                .border(1.dp, if (isSelected) NeonCyanLight else BorderSubtle, RoundedCornerShape(10.dp))
                                .clickable { onUpdateCaption(caption.copy(transition = effect)) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("effect_${effect.name}")
                        ) {
                            Text(
                                text = effect.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) DeepBlack else TextPrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // Toggle 2: Randomize rotation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Randomize rotation",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                Text(
                    text = "Tilt angle subtly per word (-8° to +8°)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Switch(
                checked = caption.randomizeRotation,
                onCheckedChange = { onUpdateCaption(caption.copy(randomizeRotation = it)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NeonCyan,
                    checkedTrackColor = Color(0xFF004D59)
                ),
                modifier = Modifier.testTag("toggle_randomize_rotation")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Toggle 3: Randomize movement
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Randomize movement",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                Text(
                    text = "Kinetic jitter & organic drift effect",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Switch(
                checked = caption.randomizeMovement,
                onCheckedChange = { onUpdateCaption(caption.copy(randomizeMovement = it)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NeonCyan,
                    checkedTrackColor = Color(0xFF004D59)
                ),
                modifier = Modifier.testTag("toggle_randomize_movement")
            )
        }
    }
}

@Composable
private fun MaskControlSection(
    caption: CaptionItem,
    onUpdateCaption: (CaptionItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Blending Mode (CapCut-style: Screen, Overlay, Normal)
        Text("CapCut Blending Modes", style = MaterialTheme.typography.labelMedium, color = NeonCyan)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BlendModeType.entries.forEach { mode ->
                val isSelected = caption.blendMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) NeonCyan else CardDarkElevated)
                        .border(1.dp, if (isSelected) NeonCyanLight else BorderSubtle, RoundedCornerShape(10.dp))
                        .clickable { onUpdateCaption(caption.copy(blendMode = mode)) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) DeepBlack else TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Masking Shapes
        Text("Mask Shape", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MaskShapeType.entries.forEach { shape ->
                val isSelected = caption.maskShape == shape
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) NeonPurple else CardDarkElevated)
                        .border(1.dp, if (isSelected) Color(0xFFD8B4FE) else BorderSubtle, RoundedCornerShape(10.dp))
                        .clickable { onUpdateCaption(caption.copy(maskShape = shape)) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = shape.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun KeyframeControlSection(
    caption: CaptionItem,
    currentPlayheadMs: Long,
    onUpdateCaption: (CaptionItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Keyframes (${caption.keyframes.size} active)",
                style = MaterialTheme.typography.labelMedium,
                color = NeonCyan
            )
            Button(
                onClick = {
                    val newKeyframe = Keyframe(
                        timeMs = currentPlayheadMs,
                        x = caption.x,
                        y = caption.y,
                        scale = caption.scale,
                        rotation = caption.rotation
                    )
                    val updated = (caption.keyframes.filter { it.timeMs != currentPlayheadMs } + newKeyframe)
                        .sortedBy { it.timeMs }
                    onUpdateCaption(caption.copy(keyframes = updated))
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = DeepBlack
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("add_keyframe_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ Keyframe at Playhead", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // X Coordinate Adjustment
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("X Position: ${caption.x.toInt()}px", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                FilledTonalIconButton(
                    onClick = { onUpdateCaption(caption.copy(x = caption.x - 15f)) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Text("-", fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                FilledTonalIconButton(
                    onClick = { onUpdateCaption(caption.copy(x = caption.x + 15f)) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Text("+", fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Y Coordinate Adjustment
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Y Position: ${caption.y.toInt()}px", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                FilledTonalIconButton(
                    onClick = { onUpdateCaption(caption.copy(y = caption.y - 15f)) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Text("-", fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                FilledTonalIconButton(
                    onClick = { onUpdateCaption(caption.copy(y = caption.y + 15f)) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Text("+", fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
        }

        if (caption.keyframes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = { onUpdateCaption(caption.copy(keyframes = emptyList())) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonPink),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Clear All Keyframes", fontSize = 12.sp)
            }
        }
    }
}
