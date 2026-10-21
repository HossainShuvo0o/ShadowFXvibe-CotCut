package com.shadowfxvibe.cotcut.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shadowfxvibe.cotcut.model.BlendModeType
import com.shadowfxvibe.cotcut.model.CaptionItem
import com.shadowfxvibe.cotcut.model.TransitionType
import com.shadowfxvibe.cotcut.model.TrendingStyle
import com.shadowfxvibe.cotcut.ui.theme.*
import com.shadowfxvibe.cotcut.util.FontHelper

@Composable
fun HomeScreen(
    onImportVideo: () -> Unit,
    onStartProjectWithStyle: (TrendingStyle) -> Unit,
    onOpenDefaultProject: () -> Unit
) {
    val categories = listOf("Trending", "New", "Bold", "Polished", "Minimal")
    var selectedCategory by remember { mutableStateOf("Trending") }

    val trendingStyles = listOf(
        TrendingStyle(
            id = "style_1",
            category = "Trending",
            title = "Cyber Neon Pop",
            previewText = "MAKE IT POP",
            fontName = "Poppins",
            color = 0xFF00E5FF,
            transition = TransitionType.Pop,
            blendMode = BlendModeType.Normal,
            tag = "VIRAL"
        ),
        TrendingStyle(
            id = "style_2",
            category = "Trending",
            title = "Kinetic Zoom Cut",
            previewText = "SPEED & FLOW",
            fontName = "Montserrat",
            color = 0xFFFF007F,
            transition = TransitionType.ZoomIn,
            blendMode = BlendModeType.Screen,
            tag = "TOP"
        ),
        TrendingStyle(
            id = "style_3",
            category = "New",
            title = "Aesthetic Glow",
            previewText = "DREAMY VIBE",
            fontName = "Shrikhand",
            color = 0xFFFFD600,
            transition = TransitionType.Scale,
            blendMode = BlendModeType.Overlay,
            tag = "NEW"
        ),
        TrendingStyle(
            id = "style_4",
            category = "Bold",
            title = "Heavy Impact",
            previewText = "BE UNSTOPPABLE",
            fontName = "Nosifer",
            color = 0xFFFF5722,
            transition = TransitionType.SlideDown,
            blendMode = BlendModeType.Normal,
            tag = "BOLD"
        ),
        TrendingStyle(
            id = "style_5",
            category = "Polished",
            title = "Bangla Motion",
            previewText = "আলোকিত মুহূর্ত",
            fontName = "Anek Bangla",
            color = 0xFF00E5FF,
            transition = TransitionType.Pop,
            blendMode = BlendModeType.Normal,
            tag = "REGIONAL"
        ),
        TrendingStyle(
            id = "style_6",
            category = "Minimal",
            title = "Clean Subtitle",
            previewText = "Clean & Simple",
            fontName = "Montserrat",
            color = 0xFFFFFFFF,
            transition = TransitionType.FadeIn,
            blendMode = BlendModeType.Normal,
            tag = "MINIMAL"
        )
    )

    val filteredStyles = trendingStyles.filter {
        if (selectedCategory == "Trending") true
        else it.category == selectedCategory
    }

    Scaffold(
        containerColor = DeepBlack,
        topBar = {
            HomeTopBar()
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                // Prominent "+ Import a video" Card inspired by Captions.ai
                ImportVideoHeroCard(onImportVideo = onImportVideo)
            }

            item {
                // Category Bar for Trending Styles
                Text(
                    text = "Trending Styles",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) NeonCyan else CardDarkElevated)
                                .border(1.dp, if (isSelected) NeonCyanLight else BorderSubtle, RoundedCornerShape(20.dp))
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .testTag("category_$category")
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) DeepBlack else TextSecondary
                            )
                        }
                    }
                }
            }

            // Trending Styles Cards Grid/List
            items(filteredStyles) { style ->
                TrendingStyleCard(
                    style = style,
                    onClick = { onStartProjectWithStyle(style) }
                )
            }

            item {
                // Recent Projects Quick Launch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Projects",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onOpenDefaultProject) {
                        Text("Open Demo", color = NeonCyan)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardDark)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                        .clickable { onOpenDefaultProject() }
                        .padding(16.dp)
                        .testTag("recent_project_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF1E293B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Neon Vibe Promo",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Edited 5m ago • 2 Captions • 1080p",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open",
                            tint = NeonCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun HomeTopBar() {
    Surface(
        color = DeepBlack,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(NeonCyan, NeonPurple))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = "Logo",
                        tint = DeepBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ShadowFXvibe Cot Cut",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "AI Captions & Video Studio",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan,
                        fontSize = 11.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(NeonCyan.copy(alpha = 0.15f))
                    .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "PRO",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = NeonCyan
                )
            }
        }
    }
}

@Composable
private fun ImportVideoHeroCard(onImportVideo: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CardDarkElevated,
                        CardDark
                    )
                )
            )
            .border(
                1.5.dp,
                Brush.linearGradient(
                    colors = listOf(
                        NeonCyan,
                        NeonPurple,
                        Color(0xFF2A2A2E)
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .clickable { onImportVideo() }
            .padding(20.dp)
            .testTag("import_video_card")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(NeonCyan.copy(alpha = 0.15f))
                    .border(1.5.dp, NeonCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Import Video",
                    tint = NeonCyan,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "+ Import a video",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Auto-captions, kinetic typography, transitions & keyframing",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onImportVideo,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = DeepBlack
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
                modifier = Modifier.testTag("start_new_project_button")
            ) {
                Icon(
                    imageVector = Icons.Default.VideoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Start a new project",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun TrendingStyleCard(
    style: TrendingStyle,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
            .testTag("style_card_${style.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = style.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NeonPurple.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = style.tag,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonPurple
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Text Preview in its font and color
                Text(
                    text = "\"${style.previewText}\"",
                    fontFamily = FontHelper.getFontFamily(style.fontName),
                    color = Color(style.color),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Transition: ${style.transition.displayName} • Font: ${style.fontName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(NeonCyan.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Use Style",
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
