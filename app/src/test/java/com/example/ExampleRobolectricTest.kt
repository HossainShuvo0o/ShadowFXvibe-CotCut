package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.shadowfxvibe.cotcut.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches ShadowFXvibe Cot Cut`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ShadowFXvibe Cot Cut", appName)
    }

    @Test
    fun `verify caption item default values and transitions`() {
        val caption = CaptionItem(
            text = "TEST CAPTION",
            transition = TransitionType.Pop,
            hasTransition = true,
            randomizeRotation = true,
            randomizeMovement = true
        )
        assertEquals("TEST CAPTION", caption.text)
        assertEquals(TransitionType.Pop, caption.transition)
        assertTrue(caption.hasTransition)
        assertTrue(caption.randomizeRotation)
        assertTrue(caption.randomizeMovement)
    }

    @Test
    fun `verify blend modes and masking options`() {
        val screenBlend = BlendModeType.Screen
        val overlayBlend = BlendModeType.Overlay
        assertEquals("Screen", screenBlend.displayName)
        assertEquals("Overlay", overlayBlend.displayName)

        val circleMask = MaskShapeType.Circle
        assertEquals("Circle", circleMask.displayName)
    }

    @Test
    fun `verify keyframe list sorting and timestamps`() {
        val kf1 = Keyframe(timeMs = 1000L, x = 10f, y = 20f)
        val kf2 = Keyframe(timeMs = 2500L, x = 50f, y = 80f)
        val caption = CaptionItem(
            text = "KEYFRAME MOTION",
            keyframes = listOf(kf2, kf1)
        )
        val sorted = caption.keyframes.sortedBy { it.timeMs }
        assertEquals(1000L, sorted.first().timeMs)
        assertEquals(2500L, sorted.last().timeMs)
    }

    @Test
    fun `verify export resolution options`() {
        val hd = ResolutionOption.HD_1080P
        val uhd = ResolutionOption.UHD_4K
        assertEquals(1080, hd.width)
        assertEquals(1920, hd.height)
        assertEquals(2160, uhd.width)
        assertEquals(3840, uhd.height)
    }
}
