package com.fathirverse.quran.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fathirverse.quran.domain.entities.Verse
import kotlinx.coroutines.flow.first

@Composable
fun VerseList(
    verses: List<Verse>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    bookmarkCounts: Map<Pair<Int, Int>, Int> = emptyMap(),
    initialVerse: Pair<Int, Int>? = null,
    onVerseClick: ((Verse) -> Unit)? = null,
) {
    var initialScrollDone by rememberSaveable(initialVerse) {
        mutableStateOf(false)
    }

    LaunchedEffect(initialVerse, verses) {
        if (initialVerse == null || initialScrollDone) return@LaunchedEffect

        val index = verses.indexOfFirst { verse ->
            verse.surahNumber == initialVerse.first &&
                verse.verseNumber == initialVerse.second
        }

        if (index >= 0) {
            snapshotFlow { listState.layoutInfo.viewportSize.height }
                .first { it > 0 }

            listState.animateScrollToItem(
                index = index,
                scrollOffset = -(listState.layoutInfo.viewportSize.height * 0.2f).toInt(),
            )
        }

        initialScrollDone = true
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(
            top = 8.dp,
            bottom = 8.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
        ),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        items(
            items = verses,
            key = { verse ->
                "${verse.surahNumber}-${verse.verseNumber}"
            },
            contentType = { "verse" },
        ) { verse ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                if (
                    shouldShowBismillah(
                        surahNumber = verse.surahNumber,
                        verseNumber = verse.verseNumber,
                    )
                ) {
                    BismillahItem()
                }

                AyahItem(
                    ayahNumber = verse.verseNumber,
                    arabic = verse.arabic,
                    transliteration = verse.transliteration,
                    translation = verse.translationId,
                    bookmarkCount = bookmarkCounts[verse.surahNumber to verse.verseNumber] ?: 0,
                    isHighlighted = initialVerse != null &&
                        verse.surahNumber == initialVerse.first &&
                        verse.verseNumber == initialVerse.second,
                    onClick = onVerseClick?.let { click ->
                        { click(verse) }
                    },
                )
            }
        }
    }
}

private fun shouldShowBismillah(
    surahNumber: Int,
    verseNumber: Int,
): Boolean {
    return verseNumber == 1 &&
            surahNumber != 1 &&
            surahNumber != 9
}
