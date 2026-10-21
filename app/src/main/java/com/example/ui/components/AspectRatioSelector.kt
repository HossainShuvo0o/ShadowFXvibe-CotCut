package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCharcoal
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AspectRatioSelector(
    selectedRatio: String,
    onRatioSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        "9:16" to "Reels / TikTok (9:16)",
        "1:1" to "Square (1:1)",
        "16:9" to "Landscape (16:9)"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(StudioCharcoal)
            .border(1.dp, StudioBorder, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEach { (ratio, label) ->
            val isSelected = selectedRatio == ratio
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else StudioSurface)
                    .border(
                        1.dp,
                        if (isSelected) NeonCyan else StudioBorder,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onRatioSelected(ratio) }
                    .padding(vertical = 8.dp)
                    .testTag("ratio_btn_$ratio"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ratio,
                    color = if (isSelected) NeonCyan else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}
