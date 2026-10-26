package com.fathirverse.theme.presentation

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import arrow.core.Either
import com.fathirverse.theme.FakeThemeRepository
import com.fathirverse.theme.domain.entities.ThemeMode
import com.fathirverse.theme.domain.failure.ThemeFailure
import com.fathirverse.theme.domain.usecases.GetThemeModeUseCase
import com.fathirverse.theme.domain.usecases.SetThemeModeUseCase
import com.fathirverse.theme.presentation.components.ThemeDialog
import com.fathirverse.theme.presentation.components.ThemeSettingItem
import com.fathirverse.theme.presentation.screens.DataSourceScreen
import com.fathirverse.theme.presentation.screens.SettingsScreen
import com.fathirverse.theme.presentation.util.appVersionName
import com.fathirverse.theme.presentation.util.isDark
import com.fathirverse.theme.presentation.viewmodels.ThemeViewModel
import com.fathirverse.uikit.theme.QuranVerseTheme
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ThemeComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var repository: FakeThemeRepository

    private var applied: ThemeMode? = null
    private var dismissCount = 0
    private var backCount = 0
    private var dataSourceCount = 0

    @Before
    fun setUp() {
        repository = FakeThemeRepository()
    }

    private fun setSettings() {
        val viewModel = ThemeViewModel(
            getThemeModeUseCase = GetThemeModeUseCase(repository),
            setThemeModeUseCase = SetThemeModeUseCase(repository),
        )

        composeRule.setContent {
            QuranVerseTheme {
                SettingsScreen(
                    onBackClick = { backCount++ },
                    onDataSourceClick = { dataSourceCount++ },
                    themeViewModel = viewModel,
                )
            }
        }
    }

    @Test
    fun themeDialog_listsEveryModeAndSelectsCurrentOne() {
        composeRule.setContent {
            QuranVerseTheme {
                ThemeDialog(
                    selected = ThemeMode.DARK,
                    onApply = { applied = it },
                    onDismiss = { dismissCount++ },
                )
            }
        }

        composeRule.onNodeWithText("Terang").assertIsDisplayed()
        composeRule.onNodeWithText("Sistem").assertIsDisplayed()
        composeRule.onNodeWithText("Gelap").assertIsSelected()
    }

    @Test
    fun themeDialog_applyReportsTheChosenMode() {
        composeRule.setContent {
            QuranVerseTheme {
                ThemeDialog(
                    selected = ThemeMode.SYSTEM,
                    onApply = { applied = it },
                    onDismiss = { dismissCount++ },
                )
            }
        }

        composeRule.onNodeWithText("Terang").performClick()
        composeRule.onNodeWithText("Terapkan").performClick()

        assertEquals(ThemeMode.LIGHT, applied)
    }

    @Test
    fun themeDialog_cancelDoesNotApply() {
        composeRule.setContent {
            QuranVerseTheme {
                ThemeDialog(
                    selected = ThemeMode.SYSTEM,
                    onApply = { applied = it },
                    onDismiss = { dismissCount++ },
                )
            }
        }

        composeRule.onNodeWithText("Gelap").performClick()
        composeRule.onNodeWithText("Batal").performClick()

        assertEquals(1, dismissCount)
        assertEquals(null, applied)
    }

    @Test
    fun themeSettingItem_showsLabelThenOpensDialogAndApplies() {
        composeRule.setContent {
            QuranVerseTheme {
                ThemeSettingItem(
                    themeMode = ThemeMode.LIGHT,
                    onThemeModeSelected = { applied = it },
                )
            }
        }

        composeRule.onNodeWithText("Terang").assertIsDisplayed()

        composeRule.onNodeWithText("Tema").performClick()
        composeRule.onNodeWithText("Terapkan").assertIsDisplayed()

        composeRule.onNodeWithText("Gelap").performClick()
        composeRule.onNodeWithText("Terapkan").performClick()

        assertEquals(ThemeMode.DARK, applied)
        composeRule.onNodeWithText("Terapkan").assertDoesNotExist()
    }

    @Test
    fun isDark_followsExplicitModeAndSystemSetting() {
        var light: Boolean? = null
        var dark: Boolean? = null
        var system: Boolean? = null
        var systemSetting: Boolean? = null

        composeRule.setContent {
            light = ThemeMode.LIGHT.isDark()
            dark = ThemeMode.DARK.isDark()
            system = ThemeMode.SYSTEM.isDark()
            systemSetting = isSystemInDarkTheme()
        }

        composeRule.waitForIdle()

        assertEquals(false, light)
        assertEquals(true, dark)
        assertEquals(systemSetting, system)
    }

    @Test
    fun settings_showsTitleItemsAndSavedThemeLabel() {
        repository.mode = Either.Right(ThemeMode.DARK)

        setSettings()

        composeRule.onNodeWithText("Pengaturan").assertIsDisplayed()
        composeRule.onNodeWithText("Tema").assertIsDisplayed()
        composeRule.onNodeWithText("Gelap").assertIsDisplayed()
        composeRule.onNodeWithText("Kirim masukan").assertIsDisplayed()
        composeRule.onNodeWithText("Tulis masukan lewat Gmail").assertIsDisplayed()
        composeRule.onNodeWithText("Sumber data").assertIsDisplayed()
    }

    @Test
    fun settings_loadFailure_fallsBackToSystemLabel() {
        repository.mode = Either.Left(ThemeFailure.LoadFailed)

        setSettings()

        composeRule.onNodeWithText("Sistem").assertIsDisplayed()
    }

    @Test
    fun settings_showsAppVersionWhenAvailable() {
        val versionName = composeRule.activity.appVersionName()

        setSettings()

        if (versionName != null) {
            composeRule.onNodeWithText("Versi $versionName").assertIsDisplayed()
        } else {
            composeRule.onNodeWithText("Versi", substring = true).assertDoesNotExist()
        }
    }

    @Test
    fun settings_chooseThemeAndApply_savesThroughRepository() {
        setSettings()

        composeRule.onNodeWithText("Tema").performClick()
        composeRule.onNodeWithText("Gelap").performClick()
        composeRule.onNodeWithText("Terapkan").performClick()

        composeRule.waitForIdle()
        assertEquals(ThemeMode.DARK, repository.savedMode)
    }

    @Test
    fun settings_clickDataSourceAndBack_callCallbacks() {
        setSettings()

        composeRule.onNodeWithText("Sumber data").performClick()
        composeRule.onNodeWithContentDescription("Kembali").performClick()

        assertEquals(1, dataSourceCount)
        assertEquals(1, backCount)
    }

    @Test
    fun settings_clickFeedback_opensGmailWithPrefilledMessage() {
        setSettings()

        composeRule.onNodeWithText("Kirim masukan").performClick()

        val sent = shadowOf(composeRule.activity).nextStartedActivity

        assertEquals(Intent.ACTION_SENDTO, sent.action)
        assertEquals("com.google.android.gm", sent.`package`)
        assertEquals("Masukan untuk Quran Verse", sent.getStringExtra(Intent.EXTRA_SUBJECT))
    }

    @Test
    fun dataSource_showsAppBarSectionsAndHandlesBack() {
        composeRule.setContent {
            QuranVerseTheme {
                DataSourceScreen(onBackClick = { backCount++ })
            }
        }

        composeRule.onNodeWithText("Sumber data").assertIsDisplayed()
        composeRule.onNodeWithText("Asal data").assertIsDisplayed()
        composeRule.onNodeWithText("Hak cipta").assertExists()
        composeRule.onNodeWithText("Lisensi data").assertExists()

        composeRule.onNodeWithContentDescription("Kembali").performClick()

        assertEquals(1, backCount)
    }
}
