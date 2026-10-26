
package com.fathirverse.quranverse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fathirverse.bottomnavigation.presentation.screens.BottomNavigationScreen
import com.fathirverse.theme.presentation.util.isDark
import com.fathirverse.theme.presentation.util.themeMode
import com.fathirverse.theme.presentation.viewmodels.ThemeViewModel
import com.fathirverse.uikit.theme.QuranVerseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val themeState by themeViewModel.uiState.collectAsStateWithLifecycle()
            val darkTheme = themeState.themeMode.isDark()

            val view = LocalView.current
            SideEffect {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightNavigationBars = !darkTheme
            }

            QuranVerseTheme(darkTheme = darkTheme) {
                BottomNavigationScreen()
            }
        }
    }
}
