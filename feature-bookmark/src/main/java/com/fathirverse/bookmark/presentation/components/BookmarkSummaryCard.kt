
package com.fathirverse.bookmark.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fathirverse.core.domain.entities.BookmarkSummary
import com.fathirverse.uikit.components.molecules.NumberedRow

@Composable
fun BookmarkSummaryCard(
    summary: BookmarkSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = summary.referenceName ?: "Juz ${summary.referenceNumber}"

    NumberedRow(
        number = summary.referenceNumber.toString(),
        title = title,
        subtitle = "${summary.bookmarkCount} markah",
        onClick = onClick,
        modifier = modifier,
    )
}
