
package com.fathirverse.bookmark.presentation.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

private val IndonesianMonths = listOf(
    "Januari",
    "Februari",
    "Maret",
    "April",
    "Mei",
    "Juni",
    "Juli",
    "Agustus",
    "September",
    "Oktober",
    "November",
    "Desember",
)

private val WibZone: ZoneId = ZoneId.of("Asia/Jakarta")

fun formatBookmarkDate(createdAt: String): String {
    return runCatching {
        val wib = LocalDateTime
            .parse(createdAt.trim().replace(' ', 'T'))
            .atZone(ZoneOffset.UTC)
            .withZoneSameInstant(WibZone)

        val hour = wib.hour.toString().padStart(2, '0')
        val minute = wib.minute.toString().padStart(2, '0')

        "${wib.dayOfMonth} ${IndonesianMonths[wib.monthValue - 1]} ${wib.year} • $hour:$minute WIB"
    }.getOrDefault(createdAt)
}
