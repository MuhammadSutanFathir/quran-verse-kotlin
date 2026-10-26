package com.fathirverse.quran.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fathirverse.uikit.components.atoms.BackButton
import com.fathirverse.uikit.components.organisms.AppBar
import com.fathirverse.uikit.components.organisms.AppBarBackground
import kotlinx.coroutines.launch

@Composable
fun VersePager(
    tabs: List<QuranTabItem>,
    initialTabId: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    page: @Composable (tabId: Int) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = tabs.indexOfFirst { it.id == initialTabId }.coerceAtLeast(0),
        pageCount = { tabs.size },
    )

    val currentTab = tabs[pagerState.currentPage.coerceIn(0, tabs.lastIndex)]

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        VerseAppBar(
            title = currentTab.title,
            onBackClick = onBackClick,
        )

        QuranTabRow(
            tabs = tabs,
            selectedTabId = currentTab.id,
            onTabSelected = { id ->
                val index = tabs.indexOfFirst { it.id == id }

                if (index >= 0) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            },
            scrollable = true,
            startFromRight = true,
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            key = { index -> tabs[index].id },
        ) { index ->
            page(tabs[index].id)
        }
    }
}

@Composable
fun VerseAppBar(
    title: String,
    onBackClick: () -> Unit,
) {
    AppBar(
        title = title,
        background = AppBarBackground.Primary,
        navigationIcon = {
            BackButton(onClick = onBackClick)
        },
    )
}
