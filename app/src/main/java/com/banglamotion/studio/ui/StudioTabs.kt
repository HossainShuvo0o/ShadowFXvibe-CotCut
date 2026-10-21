package com.banglamotion.studio.ui

import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglamotion.studio.model.EffectState
import com.banglamotion.studio.model.StudioDefaultData
import com.banglamotion.studio.model.StudioSubTab
import com.banglamotion.studio.model.SubtitleWord
import com.banglamotion.studio.theme.*

@Composable
fun StudioTabsSection(
    currentTab: StudioSubTab,
    onTabSelected: (StudioSubTab) -> Unit,
    // Text state
    mainText: String,
    onMainTextChange: (String) -> Unit,
    banglaText: String,
    onBanglaTextChange: (String) -> Unit,
    selectedFontName: String,
    onFontSelect: (String, FontFamily) -> Unit,
    fontSizeSp: Float,
    onFontSizeChange: (Float) -> Unit,
    hasOuterGlow: Boolean,
    onOuterGlowToggle: (Boolean) -> Unit,
    hasStroke: Boolean,
    onStrokeToggle: (Boolean) -> Unit,
    has3DExtrude: Boolean,
    on3DExtrudeToggle: (Boolean) -> Unit,
    // Animation state
    stillAnimation: String,
    onStillAnimChange: (String) -> Unit,
    textAnimation: String,
    onTextAnimChange: (String) -> Unit,
    onImagePicked: (Uri?) -> Unit,
    onRandomizeAnimation: () -> Unit,
    // Effects state
    effectsList: List<EffectState>,
    onEffectToggle: (String, Boolean) -> Unit,
    onEffectIntensityChange: (String, Float) -> Unit,
    // Captions state
    captionWords: List<SubtitleWord>,
    onAddCaptionWord: (String) -> Unit,
    burnInCaptions: Boolean,
    onBurnInToggle: (Boolean) -> Unit,
    // SFX & Audio
    selectedAudioTrack: String,
    onAudioTrackSelect: (String) -> Unit,
    audioVolume: Float,
    onAudioVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val toneGenerator = remember {
        try { ToneGenerator(AudioManager.STREAM_MUSIC, 80) } catch (_: Exception) { null }
    }

    fun playSfx(sfxType: String) {
        try {
            when (sfxType) {
                "Scream" -> toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 250)
                "Woosh" -> toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 180)
                "Glitch" -> toneGenerator?.startTone(ToneGenerator.TONE_SUP_RINGTONE, 120)
                "Bass Drop" -> toneGenerator?.startTone(ToneGenerator.TONE_CDMA_LOW_L, 300)
                "Riser" -> toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 220)
                else -> toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
            }
        } catch (_: Exception) {}
    }

    // Photo picker for Still Picture background
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        onImagePicked(uri)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Tab Header Bar
        ScrollableTabRow(
            selectedTabIndex = currentTab.ordinal,
            containerColor = Color(0xFF0A0202),
            contentColor = OrangePrimary,
            edgePadding = 8.dp,
            divider = { HorizontalDivider(color = StudioCardBorder, thickness = 1.dp) }
        ) {
            Tab(
                selected = currentTab == StudioSubTab.TOUCH_TEXT,
                onClick = { onTabSelected(StudioSubTab.TOUCH_TEXT) },
                text = { Text("Touch & Text", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (currentTab == StudioSubTab.TOUCH_TEXT) OrangePrimary else TextWhite) }
            )
            Tab(
                selected = currentTab == StudioSubTab.ANIMATIONS,
                onClick = { onTabSelected(StudioSubTab.ANIMATIONS) },
                text = { Text("Animations", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (currentTab == StudioSubTab.ANIMATIONS) OrangePrimary else TextWhite) }
            )
            Tab(
                selected = currentTab == StudioSubTab.EFFECTS_AM,
                onClick = { onTabSelected(StudioSubTab.EFFECTS_AM) },
                text = { Text("Effects (AM 4.3)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (currentTab == StudioSubTab.EFFECTS_AM) OrangePrimary else TextWhite) }
            )
            Tab(
                selected = currentTab == StudioSubTab.AUTO_CAPTION,
                onClick = { onTabSelected(StudioSubTab.AUTO_CAPTION) },
                text = { Text("Auto Caption", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (currentTab == StudioSubTab.AUTO_CAPTION) OrangePrimary else TextWhite) }
            )
            Tab(
                selected = currentTab == StudioSubTab.MUSIC_SFX,
                onClick = { onTabSelected(StudioSubTab.MUSIC_SFX) },
                text = { Text("Music & SFX", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (currentTab == StudioSubTab.MUSIC_SFX) OrangePrimary else TextWhite) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sub-Tab Content
        when (currentTab) {
            // =========================================================
            // SUB-TAB 1: TOUCH & TEXT
            // =========================================================
            StudioSubTab.TOUCH_TEXT -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Primary Text Input
                    OutlinedTextField(
                        value = mainText,
                        onValueChange = onMainTextChange,
                        label = { Text("Primary Text (Horror Bold)", color = TextGrayMuted, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = RedPrimary,
                            unfocusedBorderColor = StudioCardBorder,
                            focusedContainerColor = StudioCardBg,
                            unfocusedContainerColor = StudioCardBg
                        )
                    )

                    // Bangla Subtitle Input
                    OutlinedTextField(
                        value = banglaText,
                        onValueChange = onBanglaTextChange,
                        label = { Text("Bangla Subtitle (বাংলা সাবটাইটেল)", color = BanglaOrange, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BanglaOrange,
                            unfocusedTextColor = BanglaOrange,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = StudioCardBorder,
                            focusedContainerColor = StudioCardBg,
                            unfocusedContainerColor = StudioCardBg
                        )
                    )

                    // Quick Bengali Phrases Bar
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(StudioDefaultData.quickBengaliPhrases) { phrase ->
                            SuggestionChip(
                                onClick = { onBanglaTextChange(phrase) },
                                label = { Text(phrase, color = BanglaOrange, fontSize = 11.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = Color(0xFF1F0808)
                                ),
                                border = BorderStroke(1.dp, StudioCardBorder)
                            )
                        }
                    }

                    // Font Selection Row
                    Text("Select Font:", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(StudioDefaultData.fontOptions) { (name, font) ->
                            val isSelected = selectedFontName == name
                            FilterChip(
                                selected = isSelected,
                                onClick = { onFontSelect(name, font) },
                                label = { Text(name, fontSize = 12.sp, fontFamily = font) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = OrangePrimary,
                                    selectedLabelColor = Color.Black,
                                    containerColor = StudioCardBg,
                                    labelColor = TextWhite
                                ),
                                border = BorderStroke(1.dp, if (isSelected) OrangePrimary else StudioCardBorder)
                            )
                        }
                    }

                    // Style Switches & Size Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Font Size: ${fontSizeSp.toInt()}sp", color = TextWhite, fontSize = 12.sp)
                        Slider(
                            value = fontSizeSp,
                            onValueChange = onFontSizeChange,
                            valueRange = 16f..72f,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                            colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = RedPrimary)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilterChip(
                            selected = hasOuterGlow,
                            onClick = { onOuterGlowToggle(!hasOuterGlow) },
                            label = { Text("Red Glow", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = hasStroke,
                            onClick = { onStrokeToggle(!hasStroke) },
                            label = { Text("Black Stroke", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = has3DExtrude,
                            onClick = { on3DExtrudeToggle(!has3DExtrude) },
                            label = { Text("3D Extrude", fontSize = 11.sp) }
                        )
                    }
                }
            }

            // =========================================================
            // SUB-TAB 2: ANIMATIONS
            // =========================================================
            StudioSubTab.ANIMATIONS -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Still Picture Anim
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("1. Still Picture Anim (Ken Burns)", color = RedPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        OutlinedButton(
                            onClick = {
                                imagePickerLauncher.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                            border = BorderStroke(1.dp, RedPrimary),
                            modifier = Modifier.height(30.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Pick Image", tint = RedPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pick Image", fontSize = 11.sp)
                        }
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(StudioDefaultData.stillAnimations) { anim ->
                            val isSel = stillAnimation == anim
                            FilterChip(
                                selected = isSel,
                                onClick = { onStillAnimChange(anim) },
                                label = { Text(anim, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RedPrimary,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }

                    // 2. Text to Animation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("2. Text to Animation (Staggered 80ms)", color = OrangePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Button(
                            onClick = onRandomizeAnimation,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E0D0D)),
                            border = BorderStroke(1.dp, RedPrimary),
                            modifier = Modifier.height(30.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text("🎲 Random", fontSize = 11.sp, color = OrangeYellow)
                        }
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(StudioDefaultData.textAnimations) { anim ->
                            val isSel = textAnimation == anim
                            FilterChip(
                                selected = isSel,
                                onClick = { onTextAnimChange(anim) },
                                label = { Text(anim, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = OrangePrimary,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                }
            }

            // =========================================================
            // SUB-TAB 3: EFFECTS (AM 4.3 - Alight Motion 4.3 Effects)
            // =========================================================
            StudioSubTab.EFFECTS_AM -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Alight Motion 4.3 Shaders & Geometry Engine", color = TextGrayMuted, fontSize = 11.sp)

                    effectsList.take(6).forEach { effect ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(StudioCardBg)
                                .border(1.dp, if (effect.isEnabled) RedPrimary else StudioCardBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(effect.name, color = if (effect.isEnabled) OrangeYellow else TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("[${effect.category}]", color = RedPrimary, fontSize = 10.sp)
                                }
                                Text(effect.description, color = TextGrayMuted, fontSize = 10.sp, maxLines = 1)
                            }

                            Switch(
                                checked = effect.isEnabled,
                                onCheckedChange = { onEffectToggle(effect.name, it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = OrangePrimary
                                )
                            )
                        }
                    }
                }
            }

            // =========================================================
            // SUB-TAB 4: AUTO CAPTION (Word-by-word Kinetic Subs)
            // =========================================================
            StudioSubTab.AUTO_CAPTION -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Word-by-Word Kinetic Subtitles", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Burn In", color = TextGrayMuted, fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Switch(
                                checked = burnInCaptions,
                                onCheckedChange = onBurnInToggle,
                                colors = SwitchDefaults.colors(checkedTrackColor = RedPrimary)
                            )
                        }
                    }

                    // Words chips
                    Text("Synced Transcript:", color = BanglaOrange, fontSize = 11.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(captionWords) { word ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF2B0A1A))
                                    .border(1.dp, RedPrimary, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(word.word, color = Color.Yellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    var newWordInput by remember { mutableStateOf("") }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newWordInput,
                            onValueChange = { newWordInput = it },
                            placeholder = { Text("Add word (e.g. MOVES, NOW)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = OrangePrimary,
                                unfocusedBorderColor = StudioCardBorder
                            )
                        )
                        Button(
                            onClick = {
                                if (newWordInput.isNotBlank()) {
                                    onAddCaptionWord(newWordInput.trim())
                                    newWordInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                        ) {
                            Text("Add", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // =========================================================
            // SUB-TAB 5: MUSIC & SFX
            // =========================================================
            StudioSubTab.MUSIC_SFX -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Horror & Motion Sound Design", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                    // SFX Trigger Buttons
                    Text("Interactive SFX Triggers (Tap to play):", color = OrangeYellow, fontSize = 11.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Scream", "Woosh", "Glitch", "Bass Drop", "Riser").forEach { sfx ->
                            Button(
                                onClick = { playSfx(sfx) },
                                modifier = Modifier.weight(1f).height(34.dp),
                                contentPadding = PaddingValues(horizontal = 2.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF290808)),
                                border = BorderStroke(1.dp, RedPrimary)
                            ) {
                                Text(sfx, fontSize = 10.sp, color = TextWhite, maxLines = 1)
                            }
                        }
                    }

                    // Volume slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Volume: ${(audioVolume * 100).toInt()}%", color = TextWhite, fontSize = 12.sp)
                        Slider(
                            value = audioVolume,
                            onValueChange = onAudioVolumeChange,
                            valueRange = 0f..1f,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                            colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = RedPrimary)
                        )
                    }

                    // Ambience Track Selector
                    Text("Background Track:", color = TextWhite, fontSize = 12.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(listOf("Horror Ambience", "Cinematic Drone", "Cyberpunk Beat", "Tense Heartbeat")) { track ->
                            val isSel = selectedAudioTrack == track
                            FilterChip(
                                selected = isSel,
                                onClick = { onAudioTrackSelect(track) },
                                label = { Text(track, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RedPrimary,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
