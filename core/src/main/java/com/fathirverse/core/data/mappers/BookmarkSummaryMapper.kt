
package com.fathirverse.core.data.mappers

import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.entities.BookmarkSummary
import com.fathirverse.core.data.models.BookmarkSummaryModel

fun BookmarkSummaryModel.toEntity(
    type: BookmarkReferenceType,
) = BookmarkSummary(
    type = type,
    referenceNumber = referenceNumber,
    referenceName = referenceName,
    bookmarkCount = bookmarkCount,
)
