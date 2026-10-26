
package com.fathirverse.core.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmark_categories",
    indices = [
        Index(
            value = ["name"],
            name = "index_bookmark_categories_name",
            unique = true,
        ),
    ],
)
data class BookmarkCategoryModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(
        name = "created_at",
        defaultValue = "CURRENT_TIMESTAMP",
    )
    val createdAt: String,
)
