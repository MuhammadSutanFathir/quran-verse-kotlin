package com.fathirverse.quran.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.fathirverse.uikit.components.molecules.UnderlineTabRow
import com.fathirverse.uikit.theme.quranColors

data class QuranTabItem(
    val id: Int,
    val title: String,
)

@Composable
fun QuranTabRow(
    tabs: List<QuranTabItem>,
    selectedTabId: Int,
    onTabSelected: (Int) -> Unit,
    scrollable: Boolean = false,
    startFromRight: Boolean = false,
) {
    if (tabs.isEmpty()) return

    val selectedIndex = remember(tabs, selectedTabId) {
        tabs.indexOfFirst { it.id == selectedTabId }.coerceAtLeast(0)
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides if (startFromRight) {
            LayoutDirection.Rtl
        } else {
            LayoutDirection.Ltr
        },
    ) {
        if (scrollable) {
            LazyQuranTabs(
                tabs = tabs,
                selectedIndex = selectedIndex,
                onTabSelected = onTabSelected,
            )
        } else {
            UnderlineTabRow(
                titles = tabs.map { it.title },
                selectedIndex = selectedIndex,
                onTabSelected = { index ->
                    onTabSelected(tabs[index].id)
                },
            )
        }
    }
}

@Composable
private fun LazyQuranTabs(
    tabs: List<QuranTabItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    val containerColor = MaterialTheme.colorScheme.primary
    val contentColor = MaterialTheme.colorScheme.onPrimary
    val indicatorColor = MaterialTheme.quranColors.gold

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (selectedIndex - 1).coerceAtLeast(0),
    )

    LaunchedEffect(selectedIndex) {
        fun selectedItem() = listState.layoutInfo.visibleItemsInfo
            .firstOrNull { it.index == selectedIndex }

        if (selectedItem() == null) {
            listState.scrollToItem(selectedIndex)
        }

        val item = selectedItem() ?: return@LaunchedEffect
        val layout = listState.layoutInfo
        val viewportCenter = (layout.viewportStartOffset + layout.viewportEndOffset) / 2f

        listState.animateScrollBy(item.offset + item.size / 2f - viewportCenter)
    }

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .background(containerColor),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(
            items = tabs,
            key = { tab -> tab.id },
            contentType = { "tab" },
        ) { tab ->
            val selected = tabs.getOrNull(selectedIndex)?.id == tab.id

            Box(
                modifier = Modifier
                    .height(48.dp)
                    .clickable { onTabSelected(tab.id) }
                    .drawBehind {
                        if (selected) {
                            val thickness = 2.dp.toPx()
                            drawRect(
                                color = indicatorColor,
                                topLeft = Offset(0f, size.height - thickness),
                                size = Size(size.width, thickness),
                            )
                        }
                    }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = tab.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor.copy(
                        alpha = if (selected) 1f else 0.72f,
                    ),
                    maxLines = 1,
                )
            }
        }
    }
}