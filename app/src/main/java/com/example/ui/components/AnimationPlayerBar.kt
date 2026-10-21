package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCharcoal
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun AnimationPlayerBar(
    isPlaying: Boolean,
    currentTimeMs: Float,
    totalDurationMs: Int,
    isLoop: Boolean,
    speed: Float,
    onPlayPauseToggle: () -> Unit,
    onReplay: () -> Unit,
    onLoopToggle: () -> Unit,
    onSeek: (Float) -> Unit,
    onSpeedChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (currentTimeMs / totalDurationMs.coerceAtLeast(100)).coerceIn(0f, 1f)
    val currentTimeSec = currentTimeMs / 1000f
    val totalTimeSec = totalDurationMs / 1000f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(StudioCharcoal)
            .border(1.dp, StudioBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("animation_player_bar")
    ) {
        // Scrubber Slider and Timestamps
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = String.format(Locale.US, "%.1fs", currentTimeSec),
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(38.dp)
            )

            Slider(
                value = progress,
                onValueChange = { newProgress ->
                    onSeek(newProgress * totalDurationMs)
                },
                colors = SliderDefaults.colors(
                    thumbColor = NeonCyan,
                    activeTrackColor = NeonCyan,
                    inactiveTrackColor = StudioBorder
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .testTag("timeline_scrubber")
            )

            Text(
                text = String.format(Locale.US, "%.1fs", totalTimeSec),
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.width(38.dp)
            )
        }

        // Playback Control Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Speed toggles
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(0.5f, 1.0f, 1.5f, 2.0f).forEach { s ->
                    val isSelected = (speed == s)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else StudioSurface)
                            .border(
                                1.dp,
                                if (isSelected) NeonCyan else StudioBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onSpeedChange(s) }
                            .padding(horizontal = 7.dp, vertical = 5.dp)
                            .testTag("speed_btn_$s"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${s}x",
                            color = if (isSelected) NeonCyan else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Main playback action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Replay
                IconButton(
                    onClick = onReplay,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(StudioSurface)
                        .testTag("replay_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = "Replay Animation",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Play / Pause
                IconButton(
                    onClick = onPlayPauseToggle,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(NeonCyan)
                        .testTag("play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause Animation" else "Play Animation",
                        tint = StudioCharcoal,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Loop Toggle
                IconButton(
                    onClick = onLoopToggle,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isLoop) ElectricViolet.copy(alpha = 0.25f) else StudioSurface)
                        .border(
                            1.dp,
                            if (isLoop) ElectricViolet else StudioBorder,
                            CircleShape
                        )
                        .testTag("loop_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = if (isLoop) "Loop Enabled" else "Loop Disabled",
                        tint = if (isLoop) ElectricViolet else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
