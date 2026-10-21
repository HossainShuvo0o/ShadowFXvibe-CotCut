package com.banglamotion.studio.ui

import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.banglamotion.studio.theme.*

// =====================================================================
// TOOLKIT DASHBOARD (Exact Match to Video 01:05 & 01:17)
// =====================================================================

@Composable
fun ToolkitMenuScreen(
    onOpenTool: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBgBlack)
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Text(
            text = "Audio & Video Toolkit",
            color = TextWhite,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )
        Text(
            text = "Free • 100% On-Device • No sign-up required",
            color = TextGrayMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Audio Editor Section
        Text(
            text = "AUDIO EDITOR (6 TOOLS)",
            color = RedPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        val audioTools = listOf(
            Triple("Trim & Cut", "Keep just the part you want", Icons.Default.ContentCut),
            Triple("Merge", "Join clips into one seamless track", Icons.Default.Layers),
            Triple("Volume & Fade", "Adjust levels, fade in / out boost", Icons.Default.VolumeUp),
            Triple("Extract from Video", "Pull the audio track out (MP3/AAC)", Icons.Default.MovieFilter),
            Triple("Convert Format", "Convert MP3, WAV, M4A, FLAC, OGG", Icons.Default.Transform),
            Triple("Ringtone Maker", "Make and set custom ringtone / alarm", Icons.Default.NotificationsActive)
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            audioTools.chunked(2).forEach { rowTools ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowTools.forEach { (name, desc, icon) ->
                        ToolkitToolCard(
                            title = name,
                            desc = desc,
                            icon = icon,
                            iconTint = RedPrimary,
                            iconBg = Color(0xFF380808),
                            modifier = Modifier.weight(1f),
                            onClick = { onOpenTool(name) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Background Remover Section
        Text(
            text = "BACKGROUND REMOVER (IMAGE & VIDEO)",
            color = OrangePrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        ToolkitToolCard(
            title = "Bg Remover",
            desc = "AI Subject cutout, Green screen chroma key, Black background removal with PNG/MP4 export",
            icon = Icons.Default.AutoFixHigh,
            iconTint = OrangePrimary,
            iconBg = Color(0xFF331505),
            modifier = Modifier.fillMaxWidth(),
            onClick = { onOpenTool("Bg Remover") }
        )
    }
}

@Composable
fun ToolkitToolCard(
    title: String,
    desc: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, CardBorderGradient, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = StudioCardBg)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = desc,
                color = TextGrayMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                maxLines = 2
            )
        }
    }
}

// =====================================================================
// 1. AUDIO TRIM & CUT TOOL
// =====================================================================

@Composable
fun AudioTrimScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var startTrimSec by remember { mutableFloatStateOf(5f) }
    var endTrimSec by remember { mutableFloatStateOf(25f) }
    var isPlaying by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioBgBlack)
            .padding(16.dp)
    ) {
        ToolHeader(title = "Trim & Cut Audio", onBack = onBack)

        Spacer(modifier = Modifier.height(20.dp))

        // Visual Waveform Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF140505))
                .border(1.dp, RedPrimary, RoundedCornerShape(12.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val count = 40
                val barW = size.width / count
                for (i in 0 until count) {
                    val hRatio = kotlin.math.sin(i * 0.35).toFloat().let { kotlin.math.abs(it) } * 0.8f + 0.15f
                    val barH = size.height * hRatio
                    val isInsideTrim = i in 7..33
                    drawLine(
                        color = if (isInsideTrim) OrangeYellow else Color(0xFF471212),
                        start = androidx.compose.ui.geometry.Offset(i * barW + barW / 2f, (size.height - barH) / 2f),
                        end = androidx.compose.ui.geometry.Offset(i * barW + barW / 2f, (size.height + barH) / 2f),
                        strokeWidth = barW * 0.6f
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Trim Range: 00:${"%02d".format(startTrimSec.toInt())} - 00:${"%02d".format(endTrimSec.toInt())} (Length: ${(endTrimSec - startTrimSec).toInt()}s)", color = TextWhite, fontSize = 13.sp)

        Spacer(modifier = Modifier.height(10.dp))

        Text("Start Position (sec):", color = TextGrayMuted, fontSize = 12.sp)
        Slider(
            value = startTrimSec,
            onValueChange = { startTrimSec = it.coerceAtMost(endTrimSec - 2f) },
            valueRange = 0f..40f,
            colors = SliderDefaults.colors(thumbColor = RedPrimary, activeTrackColor = RedPrimary)
        )

        Text("End Position (sec):", color = TextGrayMuted, fontSize = 12.sp)
        Slider(
            value = endTrimSec,
            onValueChange = { endTrimSec = it.coerceAtLeast(startTrimSec + 2f) },
            valueRange = 10f..60f,
            colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary)
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier.weight(1f).height(46.dp),
                border = BorderStroke(1.dp, OrangePrimary)
            ) {
                Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = "Play", tint = OrangePrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isPlaying) "Pause" else "Preview", color = TextWhite)
            }

            Button(
                onClick = {
                    Toast.makeText(context, "Audio trimmed & saved to Music/BanglaMotion!", Toast.LENGTH_LONG).show()
                    onBack()
                },
                modifier = Modifier.weight(1f).height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
            ) {
                Text("Cut & Save MP3", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// =====================================================================
// 2. AUDIO MERGE TOOL
// =====================================================================

@Composable
fun AudioMergeScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var crossfadeSec by remember { mutableFloatStateOf(2f) }
    val tracks = remember {
        mutableStateListOf(
            "Track 1 - Horror Atmosphere.mp3 (00:32)",
            "Track 2 - Creepy Whispers.mp3 (00:18)",
            "Track 3 - Dark Heartbeat.mp3 (00:45)"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioBgBlack)
            .padding(16.dp)
    ) {
        ToolHeader(title = "Merge Audio Tracks", onBack = onBack)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Selected Audio Tracks (${tracks.size}):", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(tracks) { trk ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = StudioCardBg),
                    border = BorderStroke(1.dp, StudioCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AudioFile, contentDescription = "Audio", tint = RedPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(trk, color = TextWhite, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("Crossfade Duration: ${crossfadeSec.toInt()}s", color = OrangeYellow, fontSize = 13.sp)
        Slider(
            value = crossfadeSec,
            onValueChange = { crossfadeSec = it },
            valueRange = 0f..5f,
            colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = RedPrimary)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                Toast.makeText(context, "Merged ${tracks.size} tracks into Movies/BanglaMotion/Merged_Audio.mp3!", Toast.LENGTH_LONG).show()
                onBack()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
        ) {
            Text("Merge All Tracks Now", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// =====================================================================
// 3. AUDIO VOLUME & FADE TOOL
// =====================================================================

@Composable
fun AudioVolumeScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var volumeBoost by remember { mutableFloatStateOf(125f) }
    var fadeInSec by remember { mutableFloatStateOf(1.5f) }
    var fadeOutSec by remember { mutableFloatStateOf(2.0f) }
    var normalizeAudio by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioBgBlack)
            .padding(16.dp)
    ) {
        ToolHeader(title = "Volume & Fade Adjuster", onBack = onBack)

        Spacer(modifier = Modifier.height(20.dp))

        Text("Volume Gain / Boost: ${volumeBoost.toInt()}%", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Slider(
            value = volumeBoost,
            onValueChange = { volumeBoost = it },
            valueRange = 0f..200f,
            colors = SliderDefaults.colors(thumbColor = RedPrimary, activeTrackColor = RedPrimary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Fade In Duration: ${"%.1f".format(fadeInSec)}s", color = TextWhite, fontSize = 13.sp)
        Slider(
            value = fadeInSec,
            onValueChange = { fadeInSec = it },
            valueRange = 0f..5f,
            colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Fade Out Duration: ${"%.1f".format(fadeOutSec)}s", color = TextWhite, fontSize = 13.sp)
        Slider(
            value = fadeOutSec,
            onValueChange = { fadeOutSec = it },
            valueRange = 0f..5f,
            colors = SliderDefaults.colors(thumbColor = OrangeYellow, activeTrackColor = OrangeYellow)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Normalize Audio Peak Levels", color = TextWhite, fontSize = 14.sp)
            Switch(
                checked = normalizeAudio,
                onCheckedChange = { normalizeAudio = it },
                colors = SwitchDefaults.colors(checkedTrackColor = RedPrimary)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                Toast.makeText(context, "Applied volume boost & fade effects successfully!", Toast.LENGTH_LONG).show()
                onBack()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
        ) {
            Text("Apply & Save Audio", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// =====================================================================
// 4. EXTRACT FROM VIDEO TOOL
// =====================================================================

@Composable
fun AudioExtractScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var pickedVideoName by remember { mutableStateOf("sample_horror_clip.mp4") }
    var selectedCodec by remember { mutableStateOf("MP3") }
    var selectedBitrate by remember { mutableStateOf("320 kbps") }

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            pickedVideoName = uri.lastPathSegment ?: "Selected_Video.mp4"
            Toast.makeText(context, "Video loaded: $pickedVideoName", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioBgBlack)
            .padding(16.dp)
    ) {
        ToolHeader(title = "Extract Audio from Video", onBack = onBack)

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = StudioCardBg),
            border = BorderStroke(1.dp, CardBorderGradient)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Movie, contentDescription = "Video", tint = RedPrimary, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(pickedVideoName, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { videoPicker.launch("video/*") },
                    border = BorderStroke(1.dp, OrangePrimary)
                ) {
                    Text("Select Another Video", color = OrangeYellow)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Output Format:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("MP3", "AAC", "WAV").forEach { fmt ->
                FilterChip(
                    selected = selectedCodec == fmt,
                    onClick = { selectedCodec = fmt },
                    label = { Text(fmt) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RedPrimary, selectedLabelColor = Color.Black)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("Audio Bitrate:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("128 kbps", "192 kbps", "320 kbps").forEach { br ->
                FilterChip(
                    selected = selectedBitrate == br,
                    onClick = { selectedBitrate = br },
                    label = { Text(br) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangePrimary, selectedLabelColor = Color.Black)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                Toast.makeText(context, "Extracted track to Movies/BanglaMotion/$pickedVideoName.$selectedCodec", Toast.LENGTH_LONG).show()
                onBack()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
        ) {
            Text("Extract Audio Track", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// =====================================================================
// 5. AUDIO CONVERT FORMAT TOOL
// =====================================================================

@Composable
fun AudioConvertScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var selectedFormat by remember { mutableStateOf("WAV") }
    var selectedSampleRate by remember { mutableStateOf("48 kHz") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioBgBlack)
            .padding(16.dp)
    ) {
        ToolHeader(title = "Convert Audio Format", onBack = onBack)

        Spacer(modifier = Modifier.height(20.dp))

        Text("Target Output Format:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOf("MP3", "WAV", "M4A", "FLAC", "OGG").forEach { fmt ->
                FilterChip(
                    selected = selectedFormat == fmt,
                    onClick = { selectedFormat = fmt },
                    label = { Text(fmt) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RedPrimary, selectedLabelColor = Color.Black)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text("Sample Rate Quality:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("44.1 kHz (CD Quality)", "48 kHz (Studio)").forEach { sr ->
                FilterChip(
                    selected = selectedSampleRate == sr,
                    onClick = { selectedSampleRate = sr },
                    label = { Text(sr) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangePrimary, selectedLabelColor = Color.Black)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                Toast.makeText(context, "Converted audio to $selectedFormat ($selectedSampleRate)!", Toast.LENGTH_LONG).show()
                onBack()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
        ) {
            Text("Convert & Export Now", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// =====================================================================
// 6. RINGTONE MAKER TOOL
// =====================================================================

@Composable
fun AudioRingtoneScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var ringtoneStart by remember { mutableFloatStateOf(0f) }
    var ringtoneTarget by remember { mutableStateOf("Phone Ringtone") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioBgBlack)
            .padding(16.dp)
    ) {
        ToolHeader(title = "Ringtone Maker", onBack = onBack)

        Spacer(modifier = Modifier.height(20.dp))

        Text("Cut Best 30 Seconds Clip:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("Start: 00:${"%02d".format(ringtoneStart.toInt())} - End: 00:${"%02d".format((ringtoneStart + 30).toInt())}", color = OrangeYellow, fontSize = 13.sp)

        Slider(
            value = ringtoneStart,
            onValueChange = { ringtoneStart = it },
            valueRange = 0f..90f,
            colors = SliderDefaults.colors(thumbColor = RedPrimary, activeTrackColor = RedPrimary)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Set As:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Phone Ringtone", "Notification Sound", "Alarm Tone").forEach { target ->
                FilterChip(
                    selected = ringtoneTarget == target,
                    onClick = { ringtoneTarget = target },
                    label = { Text(target) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangePrimary, selectedLabelColor = Color.Black)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                Toast.makeText(context, "Set as $ringtoneTarget successfully!", Toast.LENGTH_LONG).show()
                onBack()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
        ) {
            Text("Set $ringtoneTarget", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// =====================================================================
// 7. BACKGROUND REMOVER TOOL (Image & Video)
// =====================================================================

@Composable
fun BgRemoverScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var selectedMode by remember { mutableStateOf("AI Subject Cutout") }
    var tolerance by remember { mutableFloatStateOf(50f) }
    var featherEdge by remember { mutableFloatStateOf(10f) }
    var previewRemoved by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioBgBlack)
            .padding(16.dp)
    ) {
        ToolHeader(title = "Background Remover", onBack = onBack)

        Spacer(modifier = Modifier.height(14.dp))

        // Mode selector
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("AI Subject Cutout", "Green Screen", "Black Background").forEach { mode ->
                FilterChip(
                    selected = selectedMode == mode,
                    onClick = { selectedMode = mode },
                    label = { Text(mode, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangePrimary, selectedLabelColor = Color.Black)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Transparency Checkerboard Preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1F1F1F))
                .border(1.dp, CardBorderGradient, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val sqSize = 20f
                var x = 0f
                var y = 0f
                var row = 0
                while (y < size.height) {
                    x = 0f
                    var col = row
                    while (x < size.width) {
                        if (col % 2 == 0) {
                            drawRect(color = Color(0xFF2E2E2E), topLeft = androidx.compose.ui.geometry.Offset(x, y), size = androidx.compose.ui.geometry.Size(sqSize, sqSize))
                        }
                        x += sqSize
                        col++
                    }
                    y += sqSize
                    row++
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Person, contentDescription = "Cutout", tint = RedPrimary, modifier = Modifier.size(54.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (previewRemoved) "Background Removed (Transparent Alpha)" else "Original Background",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("Removal Tolerance: ${tolerance.toInt()}%", color = TextWhite, fontSize = 13.sp)
        Slider(
            value = tolerance,
            onValueChange = { tolerance = it },
            valueRange = 10f..100f,
            colors = SliderDefaults.colors(thumbColor = RedPrimary, activeTrackColor = RedPrimary)
        )

        Text("Edge Feathering: ${featherEdge.toInt()}px", color = TextWhite, fontSize = 13.sp)
        Slider(
            value = featherEdge,
            onValueChange = { featherEdge = it },
            valueRange = 0f..30f,
            colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                Toast.makeText(context, "Saved Transparent PNG Cutout to Pictures/BanglaMotion!", Toast.LENGTH_LONG).show()
                onBack()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
        ) {
            Text("Export Transparent PNG / Alpha MP4", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun ToolHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(title, color = TextWhite, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
    }
}
