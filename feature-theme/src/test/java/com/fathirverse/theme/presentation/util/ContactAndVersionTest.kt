package com.fathirverse.theme.presentation.util

import android.app.Application
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContextWrapper
import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class ContactAndVersionTest {

    private val app: Application
        get() = RuntimeEnvironment.getApplication()

    private fun newActivity(): Activity {
        return Robolectric.buildActivity(Activity::class.java).create().get()
    }

    private class StartFailingContext(
        base: Application,
        private val failures: Int,
    ) : ContextWrapper(base) {

        val attempts = mutableListOf<Intent>()

        override fun startActivity(intent: Intent) {
            attempts += intent

            if (attempts.size <= failures) {
                throw ActivityNotFoundException()
            }
        }
    }

    @Test
    fun openFeedbackEmail_sendsToGmailWithAddressSubjectAndBody() {
        val activity = newActivity()

        assertTrue(activity.openFeedbackEmail("1.2.3"))

        val sent = shadowOf(activity).nextStartedActivity

        assertEquals(Intent.ACTION_SENDTO, sent.action)
        assertEquals("com.google.android.gm", sent.`package`)
        assertEquals("Masukan untuk Quran Verse", sent.getStringExtra(Intent.EXTRA_SUBJECT))
        assertEquals(
            "muhammadsutanfathir@gmail.com",
            sent.getStringArrayExtra(Intent.EXTRA_EMAIL)?.single(),
        )

        val body = sent.getStringExtra(Intent.EXTRA_TEXT).orEmpty()

        assertTrue(body.contains("Versi aplikasi: 1.2.3"))
        assertTrue(body.contains("Android: "))
        assertTrue(body.contains("Perangkat: "))
    }

    @Test
    fun openFeedbackEmail_unknownVersion_isShownAsDash() {
        val activity = newActivity()

        activity.openFeedbackEmail(null)

        val body = shadowOf(activity).nextStartedActivity
            .getStringExtra(Intent.EXTRA_TEXT)
            .orEmpty()

        assertTrue(body.contains("Versi aplikasi: -"))
    }

    @Test
    fun openFeedbackEmail_gmailMissing_fallsBackToAnyEmailApp() {
        val context = StartFailingContext(app, failures = 1)

        assertTrue(context.openFeedbackEmail("1.2.3"))

        assertEquals(2, context.attempts.size)
        assertEquals("com.google.android.gm", context.attempts[0].`package`)
        assertNull(context.attempts[1].`package`)
    }

    @Test
    fun openFeedbackEmail_noEmailAppAtAll_returnsFalse() {
        val context = StartFailingContext(app, failures = 2)

        assertFalse(context.openFeedbackEmail("1.2.3"))
        assertEquals(2, context.attempts.size)
    }

    @Test
    fun appVersionName_matchesPackageManagerValue() {
        @Suppress("DEPRECATION")
        val expected = app.packageManager.getPackageInfo(app.packageName, 0).versionName

        assertEquals(expected, app.appVersionName())
    }

    @Test
    fun appVersionName_unknownPackage_returnsNull() {
        val unknownPackage = object : ContextWrapper(app) {
            override fun getPackageName(): String = "paket.tidak.ada"
        }

        assertNull(unknownPackage.appVersionName())
    }
}
