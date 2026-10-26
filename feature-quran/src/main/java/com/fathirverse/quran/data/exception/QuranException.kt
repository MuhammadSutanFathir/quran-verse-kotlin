package com.fathirverse.quran.data.exception

import com.fathirverse.core.exception.AppException

class QuranException(
    cause: Throwable? = null,
) : AppException(cause = cause)
