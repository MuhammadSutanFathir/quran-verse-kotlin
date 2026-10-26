
package com.fathirverse.quran.presentation.events

sealed interface VerseBookmarkEvent {

    data object Saved : VerseBookmarkEvent

    data class Error(
        val message: String,
    ) : VerseBookmarkEvent
}
