package com.banglamotion.studio.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.R
import kotlin.random.Random

// =====================================================================
// PRO THEME: BLACK & RED & ORANGE GRADIENT THEME (Horror Pro Grading)
// =====================================================================

val StudioBgBlack = Color(0xFF000000)
val StudioBgDarkRed = Color(0xFF140202)
val StudioCardBg = Color(0xFF121212)
val StudioCardBorder = Color(0xFF2A1010)

val RedGlow = Color(0xFFFF1A1A)
val RedPrimary = Color(0xFFFF1A1A)
val OrangePrimary = Color(0xFFFF6A00)
val OrangeYellow = Color(0xFFFF8C00)
val BanglaOrange = Color(0xFFFFB347)
val GreenFAB = Color(0xFF00FF6A)

val TextWhite = Color(0xFFFFFFFF)
val TextGrayMuted = Color(0xFF9E9E9E)
val TextRedMuted = Color(0xFFFF8E8E)

// Gradients
val RedOrangeGradient = Brush.linearGradient(
    colors = listOf(RedPrimary, OrangePrimary, OrangeYellow)
)

val DarkHeaderGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF000000), Color(0xFF1C0202))
)

val CardBorderGradient = Brush.linearGradient(
    colors = listOf(RedPrimary.copy(alpha = 0.8f), OrangePrimary.copy(alpha = 0.6f))
)

val SaveButtonGradient = Brush.horizontalGradient(
    colors = listOf(RedPrimary, OrangePrimary)
)

val ScrubberGradient = Brush.horizontalGradient(
    colors = listOf(RedPrimary, OrangePrimary, OrangeYellow)
)

// Font Families from resources
val CreepsterFont = FontFamily(Font(R.font.creepster))
val NosiferFont = FontFamily(Font(R.font.nosifer))
val ShrikhandFont = FontFamily(Font(R.font.shrikhand))
val PoppinsFont = FontFamily(Font(R.font.poppins))
val AnekBanglaFont = FontFamily(Font(R.font.anek_bangla))
val HindSiliguriFont = FontFamily(Font(R.font.hind_siliguri))
val MontserratFont = FontFamily(Font(R.font.montserrat))

data class Particle(
    val initialX: Float,
    val initialY: Float,
    val size: Float,
    val color: Color,
    val speedX: Float,
    val speedY: Float
)

@Composable
fun AnimatedParticlesBackground(modifier: Modifier = Modifier) {
    val particles = remember {
        val random = Random(42)
        List(50) {
            Particle(
                initialX = random.nextFloat(),
                initialY = random.nextFloat(),
                size = random.nextFloat() * 4f + 2f,
                color = if (random.nextBoolean()) RedPrimary.copy(alpha = 0.35f) else OrangePrimary.copy(alpha = 0.3f),
                speedX = (random.nextFloat() - 0.5f) * 0.08f,
                speedY = -(random.nextFloat() * 0.15f + 0.02f)
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "particles")
    val animTime by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        particles.forEach { p ->
            val currX = ((p.initialX + p.speedX * animTime) % 1f + 1f) % 1f * w
            val currY = ((p.initialY + p.speedY * animTime) % 1f + 1f) % 1f * h
            val alphaFactor = (0.5f + 0.5f * kotlin.math.sin((animTime * 6.28f + p.initialX * 10f))).coerceIn(0.2f, 0.9f)

            drawCircle(
                color = p.color.copy(alpha = p.color.alpha * alphaFactor),
                radius = p.size,
                center = Offset(currX, currY)
            )
        }
    }
}

@Composable
fun BanglaMotionTheme(content: @Composable () -> Unit) {
    val darkColors = darkColorScheme(
        primary = RedPrimary,
        secondary = OrangePrimary,
        tertiary = OrangeYellow,
        background = StudioBgBlack,
        surface = StudioCardBg,
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onBackground = TextWhite,
        onSurface = TextWhite
    )

    MaterialTheme(
        colorScheme = darkColors,
        content = content
    )
}
