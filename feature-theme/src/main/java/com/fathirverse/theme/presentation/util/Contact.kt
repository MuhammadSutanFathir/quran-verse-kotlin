package com.fathirverse.theme.presentation.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build

private const val FEEDBACK_EMAIL = "muhammadsutanfathir@gmail.com"

private const val GMAIL_PACKAGE = "com.google.android.gm"

fun Context.openFeedbackEmail(versionName: String?): Boolean {
    val body = buildString {
        append("\n\n")
        append("---\n")
        append("Versi aplikasi: ${versionName ?: "-"}\n")
        append("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")
        append("Perangkat: ${Build.MANUFACTURER} ${Build.MODEL}")
    }

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(FEEDBACK_EMAIL))
        putExtra(Intent.EXTRA_SUBJECT, "Masukan untuk Quran Verse")
        putExtra(Intent.EXTRA_TEXT, body)
    }

    return try {
        startActivity(Intent(intent).setPackage(GMAIL_PACKAGE))
        true
    } catch (_: ActivityNotFoundException) {
        try {
            startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }
}
