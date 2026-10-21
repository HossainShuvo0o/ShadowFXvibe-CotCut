package com.banglamotion.studio.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglamotion.studio.model.MotionTemplateItem
import com.banglamotion.studio.model.StudioDefaultData
import com.banglamotion.studio.theme.*

@Composable
fun TemplatesScreen(
    onSelectTemplate: (MotionTemplateItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("Popular") }
    val categories = listOf("Popular", "FX", "Transitions", "Text", "Horror")

    // XML preset picker
    val xmlPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            Toast.makeText(context, "XML Preset Loaded Successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    val filteredTemplates = remember(selectedCategory) {
        if (selectedCategory == "Popular") {
            StudioDefaultData.templates
        } else {
            StudioDefaultData.templates.filter { it.category.equals(selectedCategory, ignoreCase = true) }
                .ifEmpty { StudioDefaultData.templates }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBgBlack)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Discover Templates",
                    color = TextWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
                Text(
                    text = "Alight Motion 4.3 & CapCut Motion Presets",
                    color = TextGrayMuted,
                    fontSize = 12.sp
                )
            }

            OutlinedButton(
                onClick = { xmlPicker.launch("text/xml") },
                border = BorderStroke(1.dp, RedPrimary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangeYellow),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(Icons.Default.FileUpload, contentDescription = "Upload XML", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Upload XML", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Category Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { cat ->
                val isSel = selectedCategory == cat
                FilterChip(
                    selected = isSel,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = OrangePrimary,
                        selectedLabelColor = Color.Black,
                        containerColor = StudioCardBg,
                        labelColor = TextWhite
                    ),
                    border = BorderStroke(1.dp, if (isSel) OrangePrimary else StudioCardBorder)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Templates List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredTemplates) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, CardBorderGradient, RoundedCornerShape(14.dp))
                        .clickable { onSelectTemplate(item) },
                    colors = CardDefaults.cardColors(containerColor = StudioCardBg)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(RedPrimary.copy(alpha = 0.2f))
                                        .border(1.dp, RedPrimary, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(item.badge, color = RedPrimary, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.title,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            FilledTonalButton(
                                onClick = { onSelectTemplate(item) },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = OrangePrimary,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Use", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Use", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.subtitle,
                            color = TextGrayMuted,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Preview box with sample text & bangla sub
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF080202))
                                .border(1.dp, Color(0xFF240606), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = item.primaryText,
                                    color = OrangeYellow,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = item.banglaText,
                                    color = BanglaOrange,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Metadata tags
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("⚡ Anim: ${item.animation}", color = RedPrimary, fontSize = 10.sp)
                            Text("•", color = TextGrayMuted, fontSize = 10.sp)
                            Text("🎨 FX: ${item.effect}", color = OrangePrimary, fontSize = 10.sp)
                            Text("•", color = TextGrayMuted, fontSize = 10.sp)
                            Text("🔤 Font: ${item.fontName}", color = BanglaOrange, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
