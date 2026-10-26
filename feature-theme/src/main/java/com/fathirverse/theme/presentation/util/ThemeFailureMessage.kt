package com.fathirverse.theme.presentation.util

import com.fathirverse.theme.domain.failure.ThemeFailure

fun ThemeFailure.toMessage(): String {
    return when (this) {
        ThemeFailure.LoadFailed -> "Tema gagal dimuat. Silakan coba lagi."
        ThemeFailure.SaveFailed -> "Tema gagal disimpan. Silakan coba lagi."
    }
}
