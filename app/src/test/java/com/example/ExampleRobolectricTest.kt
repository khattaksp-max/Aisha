package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.Mood
import com.example.model.VoiceMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("AISHA", appName)
    }

    @Test
    fun `mood from string returns correct mood`() {
        assertEquals(Mood.HAPPY, Mood.fromString("HAPPY"))
        assertEquals(Mood.MILDLY_JEALOUS, Mood.fromString("MILDLY_JEALOUS"))
        assertEquals(Mood.CUTE, Mood.fromString("CUTE"))
    }

    @Test
    fun `voice modes have valid configuration`() {
        val ultraCute = VoiceMode.ULTRA_CUTE
        assertNotNull(ultraCute.displayName)
        assertNotNull(ultraCute.previewPhrase)
        assertEquals("ultra_cute", ultraCute.id)
    }
}
