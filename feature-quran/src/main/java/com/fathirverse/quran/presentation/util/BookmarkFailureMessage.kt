package com.fathirverse.quran.presentation.util

import com.fathirverse.core.failure.BookmarkFailure

fun BookmarkFailure.toMessage(): String {
    return when (this) {
        BookmarkFailure.LatestLoadFailed ->
            "Markah terbaru gagal dimuat. Silakan coba lagi."

        BookmarkFailure.CategoriesLoadFailed ->
            "Daftar kategori markah gagal dimuat. Silakan coba lagi."

        BookmarkFailure.BookmarkAddFailed ->
            "Markah gagal disimpan. Silakan coba lagi."

        else -> "Terjadi kesalahan. Silakan coba lagi."
    }
}
