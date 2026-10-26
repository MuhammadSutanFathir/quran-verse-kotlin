package com.fathirverse.theme.domain.failure

import com.fathirverse.core.failure.AppFailure

sealed class ThemeFailure : AppFailure() {

    data object LoadFailed : ThemeFailure()

    data object SaveFailed : ThemeFailure()
}
