package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EditorState
import com.example.data.model.TypographyOptions
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HindSiliguriFamily
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCharcoal
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TypographyControls(
    state: EditorState,
    onStateChange: (EditorState) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("টেক্সট ও ফন্ট", "রং ও স্টাইল", "ইফেক্টস", "অ্যানিমেশন")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(StudioCharcoal)
            .border(1.dp, StudioBorder, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .padding(14.dp)
            .testTag("typography_controls_panel")
    ) {
        // Text Input Field
        OutlinedTextField(
            value = state.text,
            onValueChange = { onStateChange(state.copy(text = it)) },
            label = { Text("বাংলা বা ইংরেজি টেক্সট লিখুন", color = TextSecondary) },
            singleLine = false,
            maxLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = StudioBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = NeonCyan
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("text_input_field")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Bengali phrase suggestions
        BengaliPhraseQuickBar(
            onPhraseSelected = { onStateChange(state.copy(text = it)) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Navigation Tabs for Controls
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = StudioSurface,
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NeonCyan,
                    height = 2.dp
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, StudioBorder, RoundedCornerShape(10.dp))
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontFamily = HindSiliguriFamily,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) NeonCyan else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("control_tab_$index")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scrollable content inside the active tab
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // TAB 0: Text & Font
                    TextAndFontTab(state, onStateChange)
                }

                1 -> {
                    // TAB 1: Colors & Style
                    ColorsAndStyleTab(state, onStateChange)
                }

                2 -> {
                    // TAB 2: Effects (Stroke & Shadow)
                    EffectsTab(state, onStateChange)
                }

                3 -> {
                    // TAB 3: Animation Engine Settings
                    AnimationTab(state, onStateChange)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TextAndFontTab(
    state: EditorState,
    onStateChange: (EditorState) -> Unit
) {
    // Font Family Selector
    Text(
        text = "ফন্ট নির্বাচন (Font Family)",
        color = TextPrimary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        TypographyOptions.fonts.forEach { fontOption ->
            val isSelected = state.fontFamilyName.equals(fontOption.name, ignoreCase = true)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else StudioSurface)
                    .border(
                        1.dp,
                        if (isSelected) NeonCyan else StudioBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onStateChange(state.copy(fontFamilyName = fontOption.name)) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("font_chip_${fontOption.name}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = fontOption.displayName,
                    fontFamily = fontOption.fontFamily,
                    fontSize = 12.sp,
                    color = if (isSelected) NeonCyan else TextPrimary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }

    // Font Size Slider
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("ফন্ট সাইজ (Font Size)", color = TextPrimary, fontSize = 13.sp)
        Text("${state.fontSizeSp.toInt()} sp", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
    Slider(
        value = state.fontSizeSp,
        onValueChange = { onStateChange(state.copy(fontSizeSp = it)) },
        valueRange = 16f..64f,
        colors = SliderDefaults.colors(
            thumbColor = NeonCyan,
            activeTrackColor = NeonCyan,
            inactiveTrackColor = StudioBorder
        ),
        modifier = Modifier.testTag("font_size_slider")
    )

    // Bold, Italic, Alignment Row
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bold & Italic Toggles
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Bold
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (state.isBold) NeonCyan.copy(alpha = 0.25f) else StudioSurface)
                    .border(1.dp, if (state.isBold) NeonCyan else StudioBorder, RoundedCornerShape(8.dp))
                    .clickable { onStateChange(state.copy(isBold = !state.isBold)) }
                    .testTag("bold_toggle_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FormatBold,
                    contentDescription = "Bold",
                    tint = if (state.isBold) NeonCyan else TextSecondary
                )
            }

            // Italic
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (state.isItalic) NeonCyan.copy(alpha = 0.25f) else StudioSurface)
                    .border(1.dp, if (state.isItalic) NeonCyan else StudioBorder, RoundedCornerShape(8.dp))
                    .clickable { onStateChange(state.copy(isItalic = !state.isItalic)) }
                    .testTag("italic_toggle_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FormatItalic,
                    contentDescription = "Italic",
                    tint = if (state.isItalic) NeonCyan else TextSecondary
                )
            }
        }

        // Text Alignment buttons
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(StudioSurface)
                .border(1.dp, StudioBorder, RoundedCornerShape(8.dp))
                .padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            listOf(
                Triple("Left", Icons.Default.FormatAlignLeft, "Align Left"),
                Triple("Center", Icons.Default.FormatAlignCenter, "Align Center"),
                Triple("Right", Icons.Default.FormatAlignRight, "Align Right")
            ).forEach { (align, icon, desc) ->
                val isSelected = state.textAlign == align
                IconButton(
                    onClick = { onStateChange(state.copy(textAlign = align)) },
                    modifier = Modifier
                        .size(38.dp)
                        .background(if (isSelected) NeonCyan.copy(alpha = 0.25f) else Color.Transparent)
                        .testTag("align_btn_$align")
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = desc,
                        tint = if (isSelected) NeonCyan else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Letter Spacing Slider
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("অক্ষরের ব্যবধান (Letter Spacing)", color = TextPrimary, fontSize = 13.sp)
        Text(String.format(Locale.US, "%.1f sp", state.letterSpacingSp), color = NeonCyan, fontSize = 13.sp)
    }
    Slider(
        value = state.letterSpacingSp,
        onValueChange = { onStateChange(state.copy(letterSpacingSp = it)) },
        valueRange = -1.0f..6.0f,
        colors = SliderDefaults.colors(
            thumbColor = NeonCyan,
            activeTrackColor = NeonCyan,
            inactiveTrackColor = StudioBorder
        ),
        modifier = Modifier.testTag("letter_spacing_slider")
    )
}

@Composable
private fun ColorsAndStyleTab(
    state: EditorState,
    onStateChange: (EditorState) -> Unit
) {
    // Text Color
    Text("টেক্সট রং (Text Color)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    ColorPickerRow(
        selectedHex = state.textColorHex,
        colors = TypographyOptions.presetColors,
        onColorSelected = { onStateChange(state.copy(textColorHex = it)) }
    )

    // Canvas Background Color
    Text("ক্যানভাস ব্যাকগ্রাউন্ড (Canvas Background)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    ColorPickerRow(
        selectedHex = state.canvasBgColorHex,
        colors = TypographyOptions.presetBackgroundColors,
        onColorSelected = { onStateChange(state.copy(canvasBgColorHex = it)) }
    )

    // Text Background Highlight Badge Toggle
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("টেক্সট ব্যাজ হাইলাইট (Text Box Badge)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text("সোশ্যাল রিলস এবং হেডলাইনের জন্য উপযোগী", color = TextMuted, fontSize = 11.sp)
        }
        Switch(
            checked = state.hasTextBg,
            onCheckedChange = { onStateChange(state.copy(hasTextBg = it)) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeonCyan,
                checkedTrackColor = NeonCyan.copy(alpha = 0.4f),
                uncheckedTrackColor = StudioBorder
            ),
            modifier = Modifier.testTag("text_bg_switch")
        )
    }

    if (state.hasTextBg) {
        Text("ব্যাজ ব্যাকগ্রাউন্ড রং", color = TextSecondary, fontSize = 12.sp)
        ColorPickerRow(
            selectedHex = state.textBgColorHex,
            colors = TypographyOptions.presetBackgroundColors,
            onColorSelected = { onStateChange(state.copy(textBgColorHex = it)) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("কর্নার রাউন্ড (Corner Radius)", color = TextSecondary, fontSize = 12.sp)
            Text("${state.textBgCornerRadiusDp.toInt()} dp", color = NeonCyan, fontSize = 12.sp)
        }
        Slider(
            value = state.textBgCornerRadiusDp,
            onValueChange = { onStateChange(state.copy(textBgCornerRadiusDp = it)) },
            valueRange = 0f..28f,
            colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
        )
    }

    // Text Opacity Slider
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("স্বচ্ছতা (Text Opacity)", color = TextPrimary, fontSize = 13.sp)
        Text("${(state.opacity * 100).toInt()}%", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
    Slider(
        value = state.opacity,
        onValueChange = { onStateChange(state.copy(opacity = it)) },
        valueRange = 0.1f..1.0f,
        colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan),
        modifier = Modifier.testTag("opacity_slider")
    )
}

@Composable
private fun EffectsTab(
    state: EditorState,
    onStateChange: (EditorState) -> Unit
) {
    // Stroke / Outline Control
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("টেক্সট স্ট্রোক / বর্ডার (Text Stroke)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text("নিয়ন ও সিনেমাটিক আউটলাইন লুক", color = TextMuted, fontSize = 11.sp)
        }
        Switch(
            checked = state.hasStroke,
            onCheckedChange = { onStateChange(state.copy(hasStroke = it)) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeonCyan,
                checkedTrackColor = NeonCyan.copy(alpha = 0.4f)
            ),
            modifier = Modifier.testTag("stroke_switch")
        )
    }

    if (state.hasStroke) {
        Text("স্ট্রোক রং (Stroke Color)", color = TextSecondary, fontSize = 12.sp)
        ColorPickerRow(
            selectedHex = state.strokeColorHex,
            colors = TypographyOptions.presetColors,
            onColorSelected = { onStateChange(state.copy(strokeColorHex = it)) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("স্ট্রোক উইথ (Stroke Width)", color = TextSecondary, fontSize = 12.sp)
            Text(String.format(Locale.US, "%.1f dp", state.strokeWidthDp), color = NeonCyan, fontSize = 12.sp)
        }
        Slider(
            value = state.strokeWidthDp,
            onValueChange = { onStateChange(state.copy(strokeWidthDp = it)) },
            valueRange = 0.5f..5.0f,
            colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
        )
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Shadow Control
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("শ্যাডো ও গ্লো (Shadow & Glow)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text("টেক্সটের গভীরতা ও দৃশ্যমানতা বৃদ্ধি", color = TextMuted, fontSize = 11.sp)
        }
        Switch(
            checked = state.hasShadow,
            onCheckedChange = { onStateChange(state.copy(hasShadow = it)) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = ElectricViolet,
                checkedTrackColor = ElectricViolet.copy(alpha = 0.4f)
            ),
            modifier = Modifier.testTag("shadow_switch")
        )
    }

    if (state.hasShadow) {
        Text("শ্যাডো রং", color = TextSecondary, fontSize = 12.sp)
        ColorPickerRow(
            selectedHex = state.shadowColorHex,
            colors = TypographyOptions.presetColors,
            onColorSelected = { onStateChange(state.copy(shadowColorHex = it)) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("ব্লার ব্যাসার্ধ (Blur Radius)", color = TextSecondary, fontSize = 12.sp)
            Text("${state.shadowBlurDp.toInt()} dp", color = ElectricViolet, fontSize = 12.sp)
        }
        Slider(
            value = state.shadowBlurDp,
            onValueChange = { onStateChange(state.copy(shadowBlurDp = it)) },
            valueRange = 0f..25f,
            colors = SliderDefaults.colors(thumbColor = ElectricViolet, activeTrackColor = ElectricViolet)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AnimationTab(
    state: EditorState,
    onStateChange: (EditorState) -> Unit
) {
    Text(
        text = "অ্যানিমেশন ইফেক্ট নির্বাচন করুন",
        color = TextPrimary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TypographyOptions.animations.forEach { (type, label) ->
            val isSelected = state.animationType.equals(type, ignoreCase = true)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) NeonCyan.copy(alpha = 0.25f) else StudioSurface)
                    .border(
                        1.dp,
                        if (isSelected) NeonCyan else StudioBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onStateChange(state.copy(animationType = type)) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("anim_chip_$type"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontFamily = HindSiliguriFamily,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) NeonCyan else TextPrimary
                )
            }
        }
    }

    // Duration Slider
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("অ্যানিমেশন সময়কাল (Duration)", color = TextPrimary, fontSize = 13.sp)
        Text(String.format(Locale.US, "%.1f sec", state.durationMs / 1000f), color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
    Slider(
        value = state.durationMs.toFloat(),
        onValueChange = { onStateChange(state.copy(durationMs = it.toInt())) },
        valueRange = 600f..5000f,
        colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan),
        modifier = Modifier.testTag("duration_slider")
    )

    // Delay Slider
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("প্রারম্ভিক বিলম্ব (Start Delay)", color = TextPrimary, fontSize = 13.sp)
        Text(String.format(Locale.US, "%.1f sec", state.delayMs / 1000f), color = NeonCyan, fontSize = 13.sp)
    }
    Slider(
        value = state.delayMs.toFloat(),
        onValueChange = { onStateChange(state.copy(delayMs = it.toInt())) },
        valueRange = 0f..1500f,
        colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan),
        modifier = Modifier.testTag("delay_slider")
    )
}
