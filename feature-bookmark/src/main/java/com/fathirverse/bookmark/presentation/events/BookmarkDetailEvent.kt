
package com.fathirverse.bookmark.presentation.events

sealed interface BookmarkDetailEvent {

    data object LastItemDeleted : BookmarkDetailEvent

    data class OperationError(
        val message: String,
    ) : BookmarkDetailEvent
}
