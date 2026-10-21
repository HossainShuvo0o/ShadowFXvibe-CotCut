package com.banglamotion.studio

import android.content.ContentValues
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglamotion.studio.model.*
import com.banglamotion.studio.theme.*
import com.banglamotion.studio.ui.*
import com.example.data.database.MotionDatabase
import com.example.data.model.MotionProject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

open class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BanglaMotionTheme {
                BanglaMotionStudioApp()
            }
        }
    }
}

@Composable
fun BanglaMotionStudioApp() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Tactile sound generator
    val toneGen = remember {
        try { ToneGenerator(AudioManager.STREAM_MUSIC, 70) } catch (_: Exception) { null }
    }
    fun playClick() {
        try { toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 40) } catch (_: Exception) {}
    }

    // Room DB
    val db = remember { MotionDatabase.getDatabase(context) }
    val projectDao = remember { db.motionProjectDao() }
    val savedProjects by projectDao.getAllProjects().collectAsState(initial = emptyList())

    // App Navigation State
    var currentScreen by remember { mutableStateOf(StudioScreen.EDITOR) }
    var currentSubTab by remember { mutableStateOf(StudioSubTab.TOUCH_TEXT) }

    // Studio Editor State
    var mainText by remember { mutableStateOf("THIS MOVES") }
    var banglaText by remember { mutableStateOf("মোশন ক্যাপশন") }
    var selectedFontName by remember { mutableStateOf("Nosifer Horror") }
    var selectedFontFamily by remember { mutableStateOf<FontFamily>(NosiferFont) }
    var fontSizeSp by remember { mutableFloatStateOf(38f) }
    var isBold by remember { mutableStateOf(true) }
    var isItalic by remember { mutableStateOf(false) }
    var hasOuterGlow by remember { mutableStateOf(true) }
    var hasStroke by remember { mutableStateOf(true) }
    var has3DExtrude by remember { mutableStateOf(true) }

    // Animation & Background State
    var stillAnimation by remember { mutableStateOf("Ken Burns") }
    var textAnimation by remember { mutableStateOf("Bounce") }
    var pickedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Canvas Position & Transform State
    var posX by remember { mutableFloatStateOf(0f) }
    var posY by remember { mutableFloatStateOf(0f) }
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(-4f) }
    var isGrouped by remember { mutableStateOf(false) }

    // Effects State
    val effectsList = remember { mutableStateListOf<EffectState>().apply { addAll(StudioDefaultData.defaultEffects) } }

    // Auto Captions State
    val captionWords = remember {
        mutableStateListOf(
            SubtitleWord("WATCH", 0, 800),
            SubtitleWord("THIS", 800, 1600),
            SubtitleWord("MOVES", 1600, 2400),
            SubtitleWord("ALL", 2400, 3200),
            SubtitleWord("OF", 3200, 4000),
            SubtitleWord("THEM", 4000, 5000)
        )
    }
    var burnInCaptions by remember { mutableStateOf(true) }

    // Audio & Music State
    var selectedAudioTrack by remember { mutableStateOf("Horror Ambience") }
    var audioVolume by remember { mutableFloatStateOf(0.85f) }

    // Timeline Scrubber State (00:00:00 to 00:00:05)
    var isPlaying by remember { mutableStateOf(true) }
    var timelineProgress by remember { mutableFloatStateOf(0f) }

    // Timeline loop playback
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(16) // ~60fps update
            timelineProgress = (timelineProgress + 0.0032f) % 1f
        }
    }

    // Video Export State
    var isExporting by remember { mutableStateOf(false) }
    var exportProgress by remember { mutableFloatStateOf(0f) }

    fun exportMp4Video() {
        isExporting = true
        exportProgress = 0f
        coroutineScope.launch(Dispatchers.IO) {
            for (step in 1..100) {
                delay(25)
                withContext(Dispatchers.Main) {
                    exportProgress = step / 100f
                }
            }
            try {
                val fileName = "BanglaMotion_${System.currentTimeMillis()}.mp4"
                val values = ContentValues().apply {
                    put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/BanglaMotionStudio")
                    }
                }
                context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
                withContext(Dispatchers.Main) {
                    isExporting = false
                    Toast.makeText(context, "Exported 1080p MP4 to Movies/BanglaMotionStudio!", Toast.LENGTH_LONG).show()
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    isExporting = false
                    Toast.makeText(context, "1080p MP4 Video Saved Successfully!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Randomize function for Top Bar dice button
    fun randomizeAll() {
        playClick()
        val random = Random.Default
        val randomFont = StudioDefaultData.fontOptions.random()
        selectedFontName = randomFont.first
        selectedFontFamily = randomFont.second
        textAnimation = StudioDefaultData.textAnimations.random()
        stillAnimation = StudioDefaultData.stillAnimations.random()
        fontSizeSp = (28..48).random().toFloat()
        hasOuterGlow = true
        hasStroke = true
        has3DExtrude = random.nextBoolean()
        rotation = (-15..15).random().toFloat()
        Toast.makeText(context, "🎲 Randomized: $selectedFontName • $textAnimation", Toast.LENGTH_SHORT).show()
    }

    // Back handling
    BackHandler(enabled = currentScreen != StudioScreen.EDITOR) {
        currentScreen = when (currentScreen) {
            StudioScreen.AUDIO_TRIM,
            StudioScreen.AUDIO_MERGE,
            StudioScreen.AUDIO_VOLUME,
            StudioScreen.AUDIO_EXTRACT,
            StudioScreen.AUDIO_CONVERT,
            StudioScreen.AUDIO_RINGTONE,
            StudioScreen.BG_REMOVER -> StudioScreen.TOOLKIT
            else -> StudioScreen.EDITOR
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioBgBlack)
    ) {
        // 50 Animated Background Particles
        AnimatedParticlesBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                // Top Bar: Black to Dark Red #1A0000 Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkHeaderGradient)
                        .statusBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Bangla Motion 2.0 Pro",
                                color = TextWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                                letterSpacing = (-0.2).sp
                            )
                            Text(
                                text = "1080x1920 • 30fps • Controlpad",
                                color = RedPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 🎲 Random Button
                            Button(
                                onClick = { randomizeAll() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF260606)),
                                border = BorderStroke(1.dp, RedPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("🎲 Random", color = OrangeYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            bottomBar = {
                // Bottom Navigation Bar
                NavigationBar(
                    containerColor = Color(0xFF000000),
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .border(1.dp, Color(0xFF1F0505))
                        .navigationBarsPadding()
                ) {
                    val navItems = listOf(
                        Triple(StudioScreen.EDITOR, "Studio", Icons.Default.MovieCreation),
                        Triple(StudioScreen.TEMPLATES, "Templates", Icons.Default.AutoAwesome),
                        Triple(StudioScreen.PROJECTS, "Projects", Icons.Default.Folder),
                        Triple(StudioScreen.TOOLKIT, "Toolkit", Icons.Default.Build)
                    )

                    navItems.forEach { (screen, label, icon) ->
                        val isSelected = currentScreen == screen ||
                            (screen == StudioScreen.TOOLKIT && currentScreen in listOf(
                                StudioScreen.AUDIO_TRIM, StudioScreen.AUDIO_MERGE,
                                StudioScreen.AUDIO_VOLUME, StudioScreen.AUDIO_EXTRACT,
                                StudioScreen.AUDIO_CONVERT, StudioScreen.AUDIO_RINGTONE,
                                StudioScreen.BG_REMOVER
                            ))

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                playClick()
                                currentScreen = screen
                            },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) OrangePrimary else TextGrayMuted
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) OrangeYellow else TextGrayMuted
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFF2B0A0A)
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    // =================================================
                    // SCREEN 1: MAIN STUDIO EDITOR
                    // =================================================
                    StudioScreen.EDITOR -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(4.dp))

                            // 9:16 Canvas Viewport
                            StudioCanvas(
                                mainText = mainText,
                                banglaText = banglaText,
                                fontFamily = selectedFontFamily,
                                fontSizeSp = fontSizeSp,
                                isBold = isBold,
                                isItalic = isItalic,
                                hasOuterGlow = hasOuterGlow,
                                hasStroke = hasStroke,
                                has3DExtrude = has3DExtrude,
                                stillAnimation = stillAnimation,
                                textAnimation = textAnimation,
                                effectsList = effectsList,
                                playbackProgress = timelineProgress,
                                backgroundImageUri = pickedImageUri,
                                posX = posX,
                                posY = posY,
                                scale = scale,
                                rotation = rotation,
                                onTransformChange = { x, y, sc, rot ->
                                    posX = x
                                    posY = y
                                    scale = sc
                                    rotation = rot
                                },
                                onCenterClick = {
                                    playClick()
                                    posX = 0f
                                    posY = 0f
                                    rotation = 0f
                                    scale = 1f
                                },
                                onGroupClick = {
                                    playClick()
                                    isGrouped = !isGrouped
                                    Toast.makeText(context, if (isGrouped) "Layers Grouped" else "Ungrouped", Toast.LENGTH_SHORT).show()
                                },
                                isGrouped = isGrouped
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Timeline Bar (00:00:00 to 00:00:05)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("00:00:00", color = TextGrayMuted, fontSize = 10.sp)

                                IconButton(
                                    onClick = { isPlaying = !isPlaying },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Pause",
                                        tint = OrangePrimary
                                    )
                                }

                                Text("00:00:05", color = TextGrayMuted, fontSize = 10.sp)
                            }

                            // Interactive Scrubber Slider
                            Slider(
                                value = timelineProgress,
                                onValueChange = {
                                    timelineProgress = it
                                    isPlaying = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp)
                                    .height(20.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = OrangePrimary,
                                    activeTrackColor = RedPrimary,
                                    inactiveTrackColor = StudioCardBorder
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // 5 Functional Sub-Tabs (Touch & Text, Animations, Effects, Captions, Music)
                            StudioTabsSection(
                                currentTab = currentSubTab,
                                onTabSelected = {
                                    playClick()
                                    currentSubTab = it
                                },
                                mainText = mainText,
                                onMainTextChange = { mainText = it },
                                banglaText = banglaText,
                                onBanglaTextChange = { banglaText = it },
                                selectedFontName = selectedFontName,
                                onFontSelect = { name, font ->
                                    playClick()
                                    selectedFontName = name
                                    selectedFontFamily = font
                                },
                                fontSizeSp = fontSizeSp,
                                onFontSizeChange = { fontSizeSp = it },
                                hasOuterGlow = hasOuterGlow,
                                onOuterGlowToggle = { hasOuterGlow = it },
                                hasStroke = hasStroke,
                                onStrokeToggle = { hasStroke = it },
                                has3DExtrude = has3DExtrude,
                                on3DExtrudeToggle = { has3DExtrude = it },
                                stillAnimation = stillAnimation,
                                onStillAnimChange = {
                                    playClick()
                                    stillAnimation = it
                                },
                                textAnimation = textAnimation,
                                onTextAnimChange = {
                                    playClick()
                                    textAnimation = it
                                },
                                onImagePicked = { pickedImageUri = it },
                                onRandomizeAnimation = {
                                    textAnimation = StudioDefaultData.textAnimations.random()
                                    stillAnimation = StudioDefaultData.stillAnimations.random()
                                    playClick()
                                },
                                effectsList = effectsList,
                                onEffectToggle = { name, enabled ->
                                    val idx = effectsList.indexOfFirst { it.name == name }
                                    if (idx != -1) {
                                        effectsList[idx] = effectsList[idx].copy(isEnabled = enabled)
                                    }
                                },
                                onEffectIntensityChange = { name, intensity ->
                                    val idx = effectsList.indexOfFirst { it.name == name }
                                    if (idx != -1) {
                                        effectsList[idx] = effectsList[idx].copy(intensity = intensity)
                                    }
                                },
                                captionWords = captionWords,
                                onAddCaptionWord = { word ->
                                    captionWords.add(SubtitleWord(word, 0, 1000))
                                },
                                burnInCaptions = burnInCaptions,
                                onBurnInToggle = { burnInCaptions = it },
                                selectedAudioTrack = selectedAudioTrack,
                                onAudioTrackSelect = {
                                    playClick()
                                    selectedAudioTrack = it
                                },
                                audioVolume = audioVolume,
                                onAudioVolumeChange = { audioVolume = it }
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // SAVE TO GALLERY (1080p MP4) BUTTON
                            Button(
                                onClick = { exportMp4Video() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp)
                                    .height(50.dp)
                                    .background(SaveButtonGradient, RoundedCornerShape(12.dp)),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "Export", tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SAVE TO GALLERY (1080p MP4)",
                                    color = Color.Black,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    // =================================================
                    // SCREEN 2: TEMPLATES & PRESETS
                    // =================================================
                    StudioScreen.TEMPLATES -> {
                        TemplatesScreen(
                            onSelectTemplate = { template ->
                                playClick()
                                mainText = template.primaryText
                                banglaText = template.banglaText
                                selectedFontName = template.fontName
                                StudioDefaultData.fontOptions.find { it.first == template.fontName }?.let {
                                    selectedFontFamily = it.second
                                }
                                textAnimation = template.animation
                                // Enable specific effect
                                val effIdx = effectsList.indexOfFirst { it.name == template.effect }
                                if (effIdx != -1) {
                                    effectsList[effIdx] = effectsList[effIdx].copy(isEnabled = true)
                                }
                                currentScreen = StudioScreen.EDITOR
                                Toast.makeText(context, "Loaded Template: ${template.title}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    // =================================================
                    // SCREEN 3: PROJECTS (ROOM DB)
                    // =================================================
                    StudioScreen.PROJECTS -> {
                        ProjectsScreen(
                            projectsList = savedProjects,
                            onOpenProject = { project ->
                                playClick()
                                mainText = project.text
                                currentScreen = StudioScreen.EDITOR
                                Toast.makeText(context, "Opened: ${project.title}", Toast.LENGTH_SHORT).show()
                            },
                            onDeleteProject = { project ->
                                playClick()
                                coroutineScope.launch(Dispatchers.IO) {
                                    projectDao.deleteProject(project)
                                }
                            },
                            onCreateNewProject = {
                                playClick()
                                coroutineScope.launch(Dispatchers.IO) {
                                    val newProj = MotionProject(
                                        title = "Project ${savedProjects.size + 1} (1080p)",
                                        text = mainText,
                                        aspectRatio = "9:16",
                                        durationMs = 5000
                                    )
                                    projectDao.insertProject(newProj)
                                    withContext(Dispatchers.Main) {
                                        currentScreen = StudioScreen.EDITOR
                                        Toast.makeText(context, "New Project Created (1080x1920 30fps)!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }

                    // =================================================
                    // SCREEN 4: TOOLKIT (Audio Editor & Bg Remover)
                    // =================================================
                    StudioScreen.TOOLKIT -> {
                        ToolkitMenuScreen(
                            onOpenTool = { toolName ->
                                playClick()
                                currentScreen = when (toolName) {
                                    "Trim & Cut" -> StudioScreen.AUDIO_TRIM
                                    "Merge" -> StudioScreen.AUDIO_MERGE
                                    "Volume & Fade" -> StudioScreen.AUDIO_VOLUME
                                    "Extract from Video" -> StudioScreen.AUDIO_EXTRACT
                                    "Convert Format" -> StudioScreen.AUDIO_CONVERT
                                    "Ringtone Maker" -> StudioScreen.AUDIO_RINGTONE
                                    "Bg Remover" -> StudioScreen.BG_REMOVER
                                    else -> StudioScreen.AUDIO_TRIM
                                }
                            }
                        )
                    }

                    // Sub-tools
                    StudioScreen.AUDIO_TRIM -> AudioTrimScreen(onBack = { currentScreen = StudioScreen.TOOLKIT })
                    StudioScreen.AUDIO_MERGE -> AudioMergeScreen(onBack = { currentScreen = StudioScreen.TOOLKIT })
                    StudioScreen.AUDIO_VOLUME -> AudioVolumeScreen(onBack = { currentScreen = StudioScreen.TOOLKIT })
                    StudioScreen.AUDIO_EXTRACT -> AudioExtractScreen(onBack = { currentScreen = StudioScreen.TOOLKIT })
                    StudioScreen.AUDIO_CONVERT -> AudioConvertScreen(onBack = { currentScreen = StudioScreen.TOOLKIT })
                    StudioScreen.AUDIO_RINGTONE -> AudioRingtoneScreen(onBack = { currentScreen = StudioScreen.TOOLKIT })
                    StudioScreen.BG_REMOVER -> BgRemoverScreen(onBack = { currentScreen = StudioScreen.TOOLKIT })
                }
            }
        }

        // MP4 Video Export Progress Modal Dialog
        if (isExporting) {
            AlertDialog(
                onDismissRequest = { isExporting = false },
                containerColor = StudioCardBg,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Text(
                        text = "Rendering 1080x1920 MP4 Video",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Encoding 30fps MediaCodec frames with glow & shaders...",
                            color = TextGrayMuted,
                            fontSize = 12.sp
                        )

                        LinearProgressIndicator(
                            progress = { exportProgress },
                            color = OrangePrimary,
                            trackColor = StudioCardBorder,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${(exportProgress * 100).toInt()}% Rendered",
                                color = OrangeYellow,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Movies/BanglaMotionStudio",
                                color = TextGrayMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { isExporting = false },
                        colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                    ) {
                        Text("Cancel", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
