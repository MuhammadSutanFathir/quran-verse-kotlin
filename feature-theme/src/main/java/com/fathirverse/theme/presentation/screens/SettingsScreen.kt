package com.fathirverse.theme.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fathirverse.theme.presentation.components.ThemeSettingItem
import com.fathirverse.theme.presentation.events.ThemeEvent
import com.fathirverse.theme.presentation.util.appVersionName
import com.fathirverse.theme.presentation.util.openFeedbackEmail
import com.fathirverse.theme.presentation.util.themeMode
import com.fathirverse.theme.presentation.viewmodels.ThemeViewModel
import com.fathirverse.uikit.components.atoms.BackButton
import com.fathirverse.uikit.components.molecules.SettingItem
import com.fathirverse.uikit.components.organisms.AppBar
import com.fathirverse.uikit.components.organisms.AppBarBackground

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onDataSourceClick: () -> Unit = {},
    themeViewModel: ThemeViewModel = hiltViewModel(
        viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner,
    ),
) {
    val uiState by themeViewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val versionName = remember { context.appVersionName() }

    LaunchedEffect(themeViewModel) {
        themeViewModel.event.collect { event ->
            when (event) {
                is ThemeEvent.OperationError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        AppBar(
            title = "Pengaturan",
            background = AppBarBackground.Primary,
            navigationIcon = {
                BackButton(onClick = onBackClick)
            },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            ThemeSettingItem(
                themeMode = uiState.themeMode,
                onThemeModeSelected = themeViewModel::setThemeMode,
            )

            SettingItem(
                title = "Kirim masukan",
                subtitle = "Tulis masukan lewat Gmail",
                onClick = {
                    if (!context.openFeedbackEmail(versionName)) {
                        Toast.makeText(
                            context,
                            "Aplikasi surel tidak ditemukan di perangkat ini.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                },
            )

            SettingItem(
                title = "Sumber data",
                subtitle = "Asal teks dan terjemahan Al-Qur'an",
                onClick = onDataSourceClick,
            )

            if (versionName != null) {
                Text(
                    text = "Versi $versionName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 16.dp,
                    ),
                )
            }

            Spacer(
                modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars),
            )
        }
    }
}
