
package com.fathirverse.bookmark.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fathirverse.core.domain.entities.BookmarkCategory

@Composable
fun CategoryFilterChips(
    categories: List<BookmarkCategory>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = categories,
            key = { category -> category.id },
        ) { category ->
            val selected = category.id == selectedCategoryId

            FilterChip(
                selected = selected,
                onClick = {
                    onCategorySelected(category.id)
                },
                label = {
                    Text(text = category.name)
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.Transparent,
                    labelColor = colorScheme.primary,
                    selectedContainerColor = colorScheme.primary,
                    selectedLabelColor = colorScheme.onPrimary,
                ),
                border = if (selected) {
                    null
                } else {
                    BorderStroke(
                        width = 1.5.dp,
                        color = colorScheme.primary,
                    )
                },
            )
        }
    }
}
