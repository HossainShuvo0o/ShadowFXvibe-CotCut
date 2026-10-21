package com.shadowfxvibe.cotcut.ui

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.shadowfxvibe.cotcut.model.*
import com.shadowfxvibe.cotcut.ui.theme.*
import com.shadowfxvibe.cotcut.util.FontHelper
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@OptIn(UnstableApi::class)
@Composable
fun EditorCanvas(
    videoUri: Uri?,
    initialCaptions: List<CaptionItem> = emptyList(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var captions by remember {
        mutableStateOf(
            if (initialCaptions.isNotEmpty()) initialCaptions
            else listOf(
                CaptionItem(
                    id = "cap_1",
                    text = "NEON MOTION VIBE",
                    x = 0f,
                    y = -60f,
                    color = 0xFF00E5FF,
                    fontFamilyName = "Poppins",
                    fontSize = 32f,
                    transition = TransitionType.Pop,
                    hasTransition = true,
                    randomizeRotation = true,
                    randomizeMovement = false,
                    startTimeMs = 0L,
                    endTimeMs = 4500L
                ),
                CaptionItem(
                    id = "cap_2",
                    text = "ShadowFXvibe Cot Cut",
                    x = 0f,
                    y = 80f,
                    color = 0xFFFF007F,
                    fontFamilyName = "Montserrat",
                    fontSize = 24f,
                    transition = TransitionType.ZoomIn,
                    hasTransition = true,
                    randomizeRotation = false,
                    randomizeMovement = true,
                    startTimeMs = 1000L,
                    endTimeMs = 5000L
                )
            )
        )
    }

    var selectedCaptionId by remember { mutableStateOf<String?>(captions.firstOrNull()?.id) }
    var isPlaying by remember { mutableStateOf(true) }
    var currentPlayheadMs by remember { mutableLongStateOf(0L) }
    val totalDurationMs = 6000L
    var showExportDialog by remember { mutableStateOf(false) }

    // ExoPlayer initialization
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
        }
    }

    LaunchedEffect(videoUri) {
        if (videoUri != null) {
            val mediaItem = MediaItem.fromUri(videoUri)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Playback loop for timeline & animations
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(33) // ~30 fps update
            currentPlayheadMs = (currentPlayheadMs + 33) % totalDurationMs
            if (videoUri != null && exoPlayer.isPlaying) {
                currentPlayheadMs = exoPlayer.currentPosition.coerceAtLeast(0L) % totalDurationMs
            }
        }
    }

    val selectedCaption = captions.find { it.id == selectedCaptionId }

    Scaffold(
        containerColor = DeepBlack,
        topBar = {
            EditorTopBar(
                onBack = onNavigateBack,
                onSaveToGallery = { showExportDialog = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Video Preview Canvas (ExoPlayer + Movable Captions)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.1f)
                    .background(Color(0xFF0A0A0C)),
                contentAlignment = Alignment.Center
            ) {
                // Video Surface
                if (videoUri != null) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = false
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Demo animated motion background when no local video imported
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF0B132B), Color(0xFF1C2541), Color(0xFF0F0F0F))
                            )
                        )
                        // Ambient neon motion grid lines
                        val timeSeconds = currentPlayheadMs / 1000f
                        val waveOffset = sin(timeSeconds * 2.5f) * 40f
                        drawLine(
                            color = NeonCyan.copy(alpha = 0.25f),
                            start = Offset(0f, size.height * 0.5f + waveOffset),
                            end = Offset(size.width, size.height * 0.5f - waveOffset),
                            strokeWidth = 3f
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(NeonPurple.copy(alpha = 0.2f), Color.Transparent),
                                center = Offset(size.width * 0.5f, size.height * 0.4f),
                                radius = size.width * 0.6f
                            ),
                            radius = size.width * 0.6f,
                            center = Offset(size.width * 0.5f, size.height * 0.4f)
                        )
                    }
                }

                // Overlay Captions with smooth dragging & no popups
                captions.forEach { caption ->
                    val isVisible = currentPlayheadMs in caption.startTimeMs..caption.endTimeMs
                    if (isVisible) {
                        RenderDraggableCaption(
                            caption = caption,
                            isSelected = caption.id == selectedCaptionId,
                            playheadMs = currentPlayheadMs,
                            onSelect = { selectedCaptionId = caption.id },
                            onDrag = { dx, dy ->
                                captions = captions.map {
                                    if (it.id == caption.id) {
                                        it.copy(x = it.x + dx, y = it.y + dy)
                                    } else it
                                }
                            },
                            onDelete = {
                                captions = captions.filterNot { it.id == caption.id }
                                if (selectedCaptionId == caption.id) {
                                    selectedCaptionId = captions.firstOrNull()?.id
                                }
                            }
                        )
                    }
                }

                // Floating Play/Pause & Time Indicator
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            isPlaying = !isPlaying
                            if (videoUri != null) {
                                if (isPlaying) exoPlayer.play() else exoPlayer.pause()
                            }
                        },
                        modifier = Modifier.size(28.dp).testTag("play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    val sec = (currentPlayheadMs / 1000f)
                    Text(
                        text = String.format("%.1fs / 6.0s", sec),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 2. Multi-Track Timeline (Video, Audio, Text Tracks)
            MultiTrackTimeline(
                tracks = listOf(
                    TimelineTrack(TrackType.Video, "Video Clip", totalDurationMs),
                    TimelineTrack(TrackType.Audio, "Audio Waveform", totalDurationMs),
                    TimelineTrack(TrackType.Text, "Captions Track", totalDurationMs)
                ),
                captions = captions,
                selectedCaptionId = selectedCaptionId,
                currentPlayheadMs = currentPlayheadMs,
                totalDurationMs = totalDurationMs,
                onSeek = { newTimeMs ->
                    currentPlayheadMs = newTimeMs
                    if (videoUri != null) {
                        exoPlayer.seekTo(newTimeMs)
                    }
                },
                onSelectCaption = { id -> selectedCaptionId = id },
                onAddCaption = {
                    val newCaption = CaptionItem(
                        text = "NEW CAPTION",
                        x = 0f,
                        y = 0f,
                        color = 0xFF00E5FF,
                        startTimeMs = currentPlayheadMs,
                        endTimeMs = (currentPlayheadMs + 3000L).coerceAtMost(totalDurationMs)
                    )
                    captions = captions + newCaption
                    selectedCaptionId = newCaption.id
                }
            )

            // 3. Advanced Control Panel Tabs
            ControlPanel(
                selectedCaption = selectedCaption,
                currentPlayheadMs = currentPlayheadMs,
                onUpdateCaption = { updated ->
                    captions = captions.map { if (it.id == updated.id) updated else it }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
            )
        }
    }

    if (showExportDialog) {
        ExportSettingsDialog(
            onDismissRequest = { showExportDialog = false },
            onExportComplete = { config ->
                // Saved cleanly without any unwanted popup loops
            }
        )
    }
}

@Composable
private fun EditorTopBar(
    onBack: () -> Unit,
    onSaveToGallery: () -> Unit
) {
    Surface(
        color = CardDark,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "ShadowFXvibe Cot Cut",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                    Text(
                        text = "Kinetic Motion Project",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Save to Gallery Button
                Button(
                    onClick = onSaveToGallery,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = DeepBlack
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("save_to_gallery_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = DeepBlack
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save to Gallery",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RenderDraggableCaption(
    caption: CaptionItem,
    isSelected: Boolean,
    playheadMs: Long,
    onSelect: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDelete: () -> Unit
) {
    // Calculate keyframe interpolated position if keyframes exist
    var computedX = caption.x
    var computedY = caption.y
    var computedScale = caption.scale
    var computedRotation = caption.rotation

    if (caption.keyframes.isNotEmpty()) {
        val sortedKf = caption.keyframes.sortedBy { it.timeMs }
        val prevKf = sortedKf.lastOrNull { it.timeMs <= playheadMs } ?: sortedKf.first()
        val nextKf = sortedKf.firstOrNull { it.timeMs > playheadMs } ?: sortedKf.last()

        if (prevKf.id == nextKf.id) {
            computedX = prevKf.x
            computedY = prevKf.y
            computedScale = prevKf.scale
            computedRotation = prevKf.rotation
        } else {
            val progress = ((playheadMs - prevKf.timeMs).toFloat() / (nextKf.timeMs - prevKf.timeMs).toFloat()).coerceIn(0f, 1f)
            computedX = prevKf.x + (nextKf.x - prevKf.x) * progress
            computedY = prevKf.y + (nextKf.y - prevKf.y) * progress
            computedScale = prevKf.scale + (nextKf.scale - prevKf.scale) * progress
            computedRotation = prevKf.rotation + (nextKf.rotation - prevKf.rotation) * progress
        }
    }

    // Transition animation calculations
    val captionAge = (playheadMs - caption.startTimeMs).coerceAtLeast(0L)
    val transitionDuration = 350f
    val transitionProgress = (captionAge / transitionDuration).coerceIn(0f, 1f)

    var animScale = 1.0f
    var animAlpha = 1.0f
    var animOffsetY = 0f

    if (caption.hasTransition) {
        when (caption.transition) {
            TransitionType.Pop -> {
                animScale = if (transitionProgress < 0.6f) 0.5f + (transitionProgress / 0.6f) * 0.7f else 1.2f - ((transitionProgress - 0.6f) / 0.4f) * 0.2f
            }
            TransitionType.ZoomIn -> {
                animScale = 0.3f + transitionProgress * 0.7f
            }
            TransitionType.ZoomOut -> {
                animScale = 1.8f - transitionProgress * 0.8f
            }
            TransitionType.Scale -> {
                animScale = 0.8f + sin(captionAge / 200f) * 0.15f
            }
            TransitionType.SlideDown -> {
                animOffsetY = (1f - transitionProgress) * -120f
            }
            TransitionType.SlideUp -> {
                animOffsetY = (1f - transitionProgress) * 120f
            }
            TransitionType.FadeIn -> {
                animAlpha = transitionProgress
            }
        }
    }

    // Randomize rotation & movement calculations
    var dynamicRotation = computedRotation
    var dynamicMoveX = 0f
    var dynamicMoveY = 0f

    if (caption.randomizeRotation) {
        val angleVariation = sin(captionAge / 180f) * 6.5f
        dynamicRotation += angleVariation
    }

    if (caption.randomizeMovement) {
        dynamicMoveX = sin(captionAge / 240f) * 12f
        dynamicMoveY = cos(captionAge / 240f) * 8f
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    (computedX + dynamicMoveX).roundToInt(),
                    (computedY + animOffsetY + dynamicMoveY).roundToInt()
                )
            }
            .pointerInput(caption.id) {
                detectDragGestures(
                    onDragStart = { onSelect() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    }
                )
            }
            .graphicsLayer {
                scaleX = computedScale * animScale
                scaleY = computedScale * animScale
                rotationZ = dynamicRotation
                alpha = animAlpha
            }
            .drawWithContent {
                drawContent()
                if (caption.blendMode == BlendModeType.Screen) {
                    drawRect(
                        color = Color(caption.color).copy(alpha = 0.25f),
                        blendMode = BlendMode.Screen
                    )
                } else if (caption.blendMode == BlendModeType.Overlay) {
                    drawRect(
                        color = Color(caption.color).copy(alpha = 0.25f),
                        blendMode = BlendMode.Overlay
                    )
                }
            }
            .testTag("caption_item_${caption.id}")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .then(
                    if (isSelected) {
                        Modifier
                            .border(1.5.dp, NeonCyan, RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    } else {
                        Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    }
                )
        ) {
            // Caption Text
            Text(
                text = caption.text,
                fontFamily = FontHelper.getFontFamily(caption.fontFamilyName),
                fontSize = caption.fontSize.sp,
                fontWeight = if (caption.isBold) FontWeight.Bold else FontWeight.Normal,
                color = Color(caption.color),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clip(
                        when (caption.maskShape) {
                            MaskShapeType.Circle -> CircleShape
                            MaskShapeType.Rectangle -> RoundedCornerShape(6.dp)
                            else -> RoundedCornerShape(0.dp)
                        }
                    )
            )

            // Selection controls (Delete tag)
            if (isSelected) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(NeonPink)
                            .clickable { onDelete() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MultiTrackTimeline(
    tracks: List<TimelineTrack>,
    captions: List<CaptionItem>,
    selectedCaptionId: String?,
    currentPlayheadMs: Long,
    totalDurationMs: Long,
    onSeek: (Long) -> Unit,
    onSelectCaption: (String) -> Unit,
    onAddCaption: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TimelineBackground)
            .border(1.dp, BorderSubtle)
            .padding(vertical = 6.dp)
    ) {
        // Timeline Header: Timecode + Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "TIMELINE",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "3 Tracks (Video • Audio • Captions)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onAddCaption,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CardDarkElevated,
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp).testTag("add_caption_timeline_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Text", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Timeline Ruler & Tracks Area
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(horizontal = 12.dp)
                .pointerInput(totalDurationMs) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val newTime = ((change.position.x / size.width.toFloat()) * totalDurationMs)
                            .coerceIn(0f, totalDurationMs.toFloat())
                            .toLong()
                        onSeek(newTime)
                    }
                }
        ) {
            val widthPx = constraints.maxWidth.toFloat()
            val playheadX = (currentPlayheadMs.toFloat() / totalDurationMs.toFloat()) * widthPx

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Time Ruler (0s, 1s, 2s, 3s, 4s, 5s, 6s)
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                ) {
                    val stepSec = 1
                    val totalSec = (totalDurationMs / 1000).toInt()
                    for (i in 0..totalSec step stepSec) {
                        val x = (i * 1000f / totalDurationMs) * size.width
                        drawLine(
                            color = BorderSubtle,
                            start = Offset(x, size.height * 0.4f),
                            end = Offset(x, size.height),
                            strokeWidth = 1.5f
                        )
                    }
                }

                // Track 1: Video Track
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(TrackVideoColor.copy(alpha = 0.5f))
                        .border(1.dp, TrackVideoColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Movie, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Main Video", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Medium)
                }

                // Track 2: Audio Track with simulated waveforms
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(TrackAudioColor.copy(alpha = 0.45f))
                        .border(1.dp, TrackAudioColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Audio (Voice & BGM)", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                    }
                }

                // Track 3: Text / Captions Track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(DeepBlack)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                ) {
                    captions.forEach { cap ->
                        val startFraction = (cap.startTimeMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
                        val endFraction = (cap.endTimeMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
                        val isSelected = cap.id == selectedCaptionId

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(endFraction - startFraction)
                                .offset { IntOffset((startFraction * widthPx).roundToInt(), 0) }
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) NeonCyan else TrackTextColor)
                                .border(1.dp, if (isSelected) Color.White else Color(0xFFA78BFA), RoundedCornerShape(4.dp))
                                .clickable { onSelectCaption(cap.id) }
                                .padding(horizontal = 6.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = cap.text,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) DeepBlack else Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Playhead Vertical Needle
            Box(
                modifier = Modifier
                    .offset { IntOffset(playheadX.roundToInt() - 8, 0) }
                    .width(16.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.TopCenter
            ) {
                // Playhead Scrubber Handle
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(NeonCyan)
                        .border(1.5.dp, DeepBlack, CircleShape)
                )
                // Playhead Line
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(NeonCyan)
                )
            }
        }
    }
}
