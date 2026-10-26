package com.fathirverse.search.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fathirverse.search.domain.entities.SearchSurah
import com.fathirverse.uikit.components.molecules.NumberedRow

@Composable
fun SearchSurahItem(
    surah: SearchSurah,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NumberedRow(
        number = surah.number.toString(),
        title = surah.nameLatin,
        subtitle = surah.nameId,
        onClick = onClick,
        modifier = modifier,
    )
}
