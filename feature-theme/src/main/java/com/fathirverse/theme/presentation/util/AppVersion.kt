package com.fathirverse.theme.presentation.util

import android.content.Context
import android.content.pm.PackageManager

@Suppress("DEPRECATION")
fun Context.appVersionName(): String? {
    return try {
        packageManager.getPackageInfo(packageName, 0).versionName
    } catch (_: PackageManager.NameNotFoundException) {
        null
    }
}
