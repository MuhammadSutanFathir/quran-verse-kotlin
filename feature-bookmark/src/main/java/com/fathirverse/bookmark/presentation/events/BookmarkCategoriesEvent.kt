
package com.fathirverse.bookmark.presentation.event

sealed interface BookmarkCategoriesEvent {

    data object AddSuccess : BookmarkCategoriesEvent

    data object EditSuccess : BookmarkCategoriesEvent

    data object DeleteSuccess : BookmarkCategoriesEvent

    data class OperationError(
        val message: String,
    ) : BookmarkCategoriesEvent
}
