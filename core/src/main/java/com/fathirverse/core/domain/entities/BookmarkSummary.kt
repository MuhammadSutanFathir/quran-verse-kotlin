
package com.fathirverse.core.domain.entities

data class BookmarkSummary(
    val type: BookmarkReferenceType,
    val referenceNumber: Int,
    val referenceName: String?,
    val bookmarkCount: Int,
)
