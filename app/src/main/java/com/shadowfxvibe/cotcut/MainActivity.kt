package com.shadowfxvibe.cotcut

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.shadowfxvibe.cotcut.model.CaptionItem
import com.shadowfxvibe.cotcut.model.TrendingStyle
import com.shadowfxvibe.cotcut.ui.EditorCanvas
import com.shadowfxvibe.cotcut.ui.HomeScreen
import com.shadowfxvibe.cotcut.ui.theme.DeepBlack
import com.shadowfxvibe.cotcut.ui.theme.ShadowFXvibeTheme

enum class Screen {
    Home,
    Editor
}

open class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShadowFXvibeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepBlack
                ) {
                    var currentScreen by remember { mutableStateOf(Screen.Home) }
                    var videoUri by remember { mutableStateOf<Uri?>(null) }
                    var initialCaptions by remember { mutableStateOf<List<CaptionItem>>(emptyList()) }

                    // Zero-permission Android Photo/Video Picker
                    val videoPickerLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.PickVisualMedia()
                    ) { uri: Uri? ->
                        if (uri != null) {
                            videoUri = uri
                            initialCaptions = listOf(
                                CaptionItem(
                                    id = "cap_imported_1",
                                    text = "SHADOW FX VIBE",
                                    x = 0f,
                                    y = -40f,
                                    color = 0xFF00E5FF,
                                    fontFamilyName = "Poppins",
                                    fontSize = 32f,
                                    isBold = true,
                                    transition = com.shadowfxvibe.cotcut.model.TransitionType.Pop,
                                    hasTransition = true,
                                    randomizeRotation = true,
                                    startTimeMs = 0L,
                                    endTimeMs = 4500L
                                )
                            )
                            currentScreen = Screen.Editor
                        }
                    }

                    when (currentScreen) {
                        Screen.Home -> {
                            HomeScreen(
                                onImportVideo = {
                                    videoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                    )
                                },
                                onStartProjectWithStyle = { style: TrendingStyle ->
                                    videoUri = null // Use motion canvas
                                    initialCaptions = listOf(
                                        CaptionItem(
                                            id = "cap_style_1",
                                            text = style.previewText,
                                            x = 0f,
                                            y = 0f,
                                            color = style.color,
                                            fontFamilyName = style.fontName,
                                            fontSize = 32f,
                                            isBold = true,
                                            transition = style.transition,
                                            hasTransition = true,
                                            blendMode = style.blendMode,
                                            randomizeRotation = true,
                                            startTimeMs = 0L,
                                            endTimeMs = 5000L
                                        )
                                    )
                                    currentScreen = Screen.Editor
                                },
                                onOpenDefaultProject = {
                                    videoUri = null
                                    initialCaptions = emptyList() // Uses rich default captions
                                    currentScreen = Screen.Editor
                                }
                            )
                        }
                        Screen.Editor -> {
                            BackHandler {
                                currentScreen = Screen.Home
                            }
                            EditorCanvas(
                                videoUri = videoUri,
                                initialCaptions = initialCaptions,
                                onNavigateBack = {
                                    currentScreen = Screen.Home
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
