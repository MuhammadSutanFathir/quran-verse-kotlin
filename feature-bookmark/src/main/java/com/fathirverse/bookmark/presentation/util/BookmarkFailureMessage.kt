package com.fathirverse.bookmark.presentation.util

import com.fathirverse.core.failure.BookmarkFailure

fun BookmarkFailure.toMessage(): String {
    return when (this) {
        BookmarkFailure.LatestLoadFailed ->
            "Markah terbaru gagal dimuat. Silakan coba lagi."

        BookmarkFailure.SurahSummariesLoadFailed ->
            "Ringkasan markah Surah gagal dimuat. Silakan coba lagi."

        BookmarkFailure.JuzSummariesLoadFailed ->
            "Ringkasan markah Juz gagal dimuat. Silakan coba lagi."

        BookmarkFailure.BookmarksLoadFailed ->
            "Daftar markah gagal dimuat. Silakan coba lagi."

        BookmarkFailure.BookmarkAddFailed ->
            "Markah gagal disimpan. Silakan coba lagi."

        BookmarkFailure.BookmarkDeleteFailed ->
            "Markah gagal dihapus. Silakan coba lagi."

        BookmarkFailure.BookmarkNotFound ->
            "Markah tidak ditemukan."

        BookmarkFailure.CategoriesLoadFailed ->
            "Daftar kategori markah gagal dimuat. Silakan coba lagi."

        BookmarkFailure.CategoryAddFailed ->
            "Kategori markah gagal ditambahkan. Pastikan nama kategori belum digunakan."

        BookmarkFailure.CategoryRenameFailed ->
            "Kategori markah gagal diubah. Pastikan nama kategori belum digunakan."

        BookmarkFailure.CategoryDeleteFailed ->
            "Kategori markah gagal dihapus. Silakan coba lagi."

        BookmarkFailure.CategoryNotFound ->
            "Kategori markah tidak ditemukan."
    }
}
