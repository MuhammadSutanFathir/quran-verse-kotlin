package com.fathirverse.quran.presentation.components

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.entities.TranslationText
import com.fathirverse.quran.presentation.components.AddBookmarkDialog
import com.fathirverse.quran.presentation.components.AddBookmarkTarget
import com.fathirverse.quran.presentation.events.VerseBookmarkEvent
import com.fathirverse.quran.presentation.viewmodels.VerseBookmarkViewModel
import com.fathirverse.quran.domain.entities.Verse

@Composable
fun hiltVerseBookmarkViewModel(
    type: BookmarkReferenceType,
    referenceNumber: Int,
): VerseBookmarkViewModel {
    return hiltViewModel(
        key = "bookmark-${type.name}-$referenceNumber",
    )
}

@Composable
fun BookmarkableVerseList(
    type: BookmarkReferenceType,
    referenceNumber: Int,
    verses: List<Verse>,
    surahName: (Int) -> String,
    listState: LazyListState,
    onManageBookmarkCategories: () -> Unit,
    modifier: Modifier = Modifier,
    initialVerse: Pair<Int, Int>? = null,
    bookmarkViewModel: VerseBookmarkViewModel = hiltVerseBookmarkViewModel(
        type = type,
        referenceNumber = referenceNumber,
    ),
) {
    val bookmarkCounts by bookmarkViewModel.bookmarkCounts.collectAsStateWithLifecycle()
    val categoriesState by bookmarkViewModel.categoriesState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    var actionVerse by remember {
        mutableStateOf<Verse?>(null)
    }

    var markVerse by remember {
        mutableStateOf<Verse?>(null)
    }

    LaunchedEffect(type, referenceNumber) {
        bookmarkViewModel.load(type, referenceNumber)
    }

    LaunchedEffect(bookmarkViewModel) {
        bookmarkViewModel.event.collect { event ->
            when (event) {
                VerseBookmarkEvent.Saved -> {
                    Toast.makeText(context, "Berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                }

                is VerseBookmarkEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    VerseList(
        verses = verses,
        listState = listState,
        modifier = modifier,
        bookmarkCounts = bookmarkCounts,
        initialVerse = initialVerse,
        onVerseClick = { verse ->
            actionVerse = verse
        },
    )

    actionVerse?.let { verse ->
        VerseActionSheet(
            onMarkClick = {
                actionVerse = null
                markVerse = verse
            },
            onCopyClick = {
                actionVerse = null
                copyToClipboard(
                    context = context,
                    text = buildVerseText(verse, surahName(verse.surahNumber)),
                )
            },
            onShareClick = {
                actionVerse = null
                shareText(
                    context = context,
                    text = buildVerseText(verse, surahName(verse.surahNumber)),
                )
            },
            onDismiss = {
                actionVerse = null
            },
        )
    }

    markVerse?.let { verse ->
        LaunchedEffect(verse) {
            bookmarkViewModel.loadCategories()
        }

        AddBookmarkDialog(
            target = AddBookmarkTarget(
                type = type,
                surahNumber = verse.surahNumber,
                surahName = surahName(verse.surahNumber),
                verseNumber = verse.verseNumber,
                juzNumber = verse.juzNumber,
            ),
            categoriesState = categoriesState,
            onSave = { categoryId, note ->
                bookmarkViewModel.save(
                    type = type,
                    referenceNumber = referenceNumber,
                    surahNumber = verse.surahNumber,
                    verseNumber = verse.verseNumber,
                    juzNumber = verse.juzNumber,
                    categoryId = categoryId,
                    note = note,
                )
                markVerse = null
            },
            onCreateCategory = {
                markVerse = null
                onManageBookmarkCategories()
            },
            onRetryCategories = bookmarkViewModel::loadCategories,
            onDismiss = {
                markVerse = null
            },
        )
    }
}

private fun buildVerseText(
    verse: Verse,
    surahName: String,
): String {
    return buildString {
        append("📖 $surahName (${verse.surahNumber}:${verse.verseNumber})\n\n")
        append("${verse.arabic.trim()}\n\n")
        append("${verse.transliteration.trim()}\n\n")
        append("\"${TranslationText.parse(verse.translationId).plainText}\"")
    }
}

private fun copyToClipboard(
    context: Context,
    text: String,
) {
    val clipboard = context.getSystemService(ClipboardManager::class.java)

    clipboard.setPrimaryClip(
        ClipData.newPlainText("Ayat", text),
    )

    Toast.makeText(context, "Ayat berhasil disalin", Toast.LENGTH_SHORT).show()
}

private fun shareText(
    context: Context,
    text: String,
) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }

    val chooser = Intent.createChooser(sendIntent, "Bagikan ayat").apply {
        if (context !is Activity) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    context.startActivity(chooser)
}
