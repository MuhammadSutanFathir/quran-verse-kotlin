package com.fathirverse.search.domain.failure

import com.fathirverse.core.failure.AppFailure

sealed class SearchFailure : AppFailure() {

    data object SurahsSearchFailed : SearchFailure()

    data object VerseGroupsSearchFailed : SearchFailure()

    data object SurahVersesLoadFailed : SearchFailure()
}
