
package com.fathirverse.core.data.mappers

import com.fathirverse.core.domain.entities.BookmarkCategory
import com.fathirverse.core.data.models.BookmarkCategoryModel

fun BookmarkCategoryModel.toEntity() = BookmarkCategory(
    id = id,
    name = name,
    createdAt = createdAt,
)
