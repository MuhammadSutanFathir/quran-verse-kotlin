package com.fathirverse.core.failure

sealed class BookmarkFailure : AppFailure() {

    data object LatestLoadFailed : BookmarkFailure()

    data object SurahSummariesLoadFailed : BookmarkFailure()

    data object JuzSummariesLoadFailed : BookmarkFailure()

    data object BookmarksLoadFailed : BookmarkFailure()

    data object BookmarkAddFailed : BookmarkFailure()

    data object BookmarkDeleteFailed : BookmarkFailure()

    data object BookmarkNotFound : BookmarkFailure()

    data object CategoriesLoadFailed : BookmarkFailure()

    data object CategoryAddFailed : BookmarkFailure()

    data object CategoryRenameFailed : BookmarkFailure()

    data object CategoryDeleteFailed : BookmarkFailure()

    data object CategoryNotFound : BookmarkFailure()
}
