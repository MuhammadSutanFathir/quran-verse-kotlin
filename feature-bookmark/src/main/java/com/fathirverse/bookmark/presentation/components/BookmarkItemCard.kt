
package com.fathirverse.bookmark.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.bookmark.presentation.util.formatBookmarkDate
import com.fathirverse.uikit.components.molecules.NumberBadge
import com.fathirverse.uikit.theme.quranColors

@Composable
fun BookmarkItemCard(
    index: Int,
    bookmark: Bookmark,
    isSurah: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    val title = if (isSurah) {
        "Ayat ${bookmark.verseNumber} • Juz ${bookmark.juzNumber}"
    } else {
        "${bookmark.surahName} • Ayat ${bookmark.verseNumber}"
    }

    BookmarkCard(
        modifier = modifier,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.quranColors.gold),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    NumberBadge(
                        text = index.toString(),
                        size = 40.dp,
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        Text(
                            text = formatBookmarkDate(bookmark.createdAt),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    IconButton(
                        onClick = onDeleteClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            modifier = Modifier.size(18.dp),
                        )
                    }

                    IconButton(
                        onClick = {
                            expanded = !expanded
                        },
                    ) {
                        Icon(
                            imageVector = if (expanded) {
                                Icons.Default.KeyboardArrowUp
                            } else {
                                Icons.Default.KeyboardArrowDown
                            },
                            contentDescription = if (expanded) {
                                "Sembunyikan catatan"
                            } else {
                                "Tampilkan catatan"
                            },
                        )
                    }
                }

                AnimatedVisibility(
                    visible = expanded,
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Catatan",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = bookmark.note?.takeIf { it.isNotBlank() }
                                ?: "Tidak ada catatan",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}
