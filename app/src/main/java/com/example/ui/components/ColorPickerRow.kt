package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.StudioBorder

@Composable
fun ColorPickerRow(
    selectedHex: String,
    colors: List<Pair<String, String>>,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        colors.forEach { (hex, name) ->
            val isSelected = selectedHex.equals(hex, ignoreCase = true)
            val color = remember(hex) { parseHexColor(hex, Color.White) }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) Color.White else StudioBorder,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(hex) }
                    .testTag("color_item_$hex"),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    val iconTint = if (hex == "#FFFFFF" || hex == "#FFD700" || hex == "#FFEB3B" || hex == "#00F0FF") {
                        Color.Black
                    } else {
                        Color.White
                    }
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected $name",
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
