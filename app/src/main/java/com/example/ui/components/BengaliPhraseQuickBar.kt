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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TypographyOptions
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HindSiliguriFamily
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.TextPrimary

@Composable
fun BengaliPhraseQuickBar(
    onPhraseSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TypographyOptions.popularBanglaPhrases.forEachIndexed { index, phrase ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(StudioSurface)
                    .border(1.dp, StudioBorder, RoundedCornerShape(20.dp))
                    .clickable { onPhraseSelected(phrase) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("phrase_chip_$index")
            ) {
                Text(
                    text = phrase,
                    fontFamily = HindSiliguriFamily,
                    fontSize = 12.sp,
                    color = TextPrimary
                )
            }
        }
    }
}
