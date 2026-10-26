package com.fathirverse.uikit.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.fathirverse.uikit.components.atoms.BackButton
import com.fathirverse.uikit.components.atoms.CircleIconButton
import com.fathirverse.uikit.components.atoms.Rosette
import com.fathirverse.uikit.components.atoms.StatusBarIcons
import com.fathirverse.uikit.components.atoms.TonalIconButton
import com.fathirverse.uikit.components.molecules.DropdownField
import com.fathirverse.uikit.components.molecules.LoadingView
import com.fathirverse.uikit.components.molecules.MessageView
import com.fathirverse.uikit.components.molecules.NumberBadge
import com.fathirverse.uikit.components.molecules.NumberedRow
import com.fathirverse.uikit.components.molecules.RadioOptionRow
import com.fathirverse.uikit.components.molecules.ReadOnlyInfoCard
import com.fathirverse.uikit.components.molecules.SettingItem
import com.fathirverse.uikit.components.molecules.TextInputField
import com.fathirverse.uikit.components.molecules.UnderlineTabRow
import com.fathirverse.uikit.components.organisms.AppBar
import com.fathirverse.uikit.components.organisms.AppBarBackground
import com.fathirverse.uikit.components.organisms.AppDialog
import com.fathirverse.uikit.components.organisms.QuranVerseAppBar
import com.fathirverse.uikit.glass.LocalGlassHolder
import com.fathirverse.uikit.glass.glassSurface
import com.fathirverse.uikit.theme.AmiriFont
import com.fathirverse.uikit.theme.AppShapes
import com.fathirverse.uikit.theme.AppTypography
import com.fathirverse.uikit.theme.DarkColorScheme
import com.fathirverse.uikit.theme.DarkQuranColors
import com.fathirverse.uikit.theme.LightColorScheme
import com.fathirverse.uikit.theme.LightQuranColors
import com.fathirverse.uikit.theme.QuranVerseTheme
import com.fathirverse.uikit.theme.RobotoFont
import com.fathirverse.uikit.theme.quranColors
import io.github.neilyich.glassmorphism.blurredContent
import io.github.neilyich.glassmorphism.rememberBlurHolder
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class UiKitComponentsTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private var clickCount = 0
    private var secondCount = 0
    private val selectedIndexes = mutableListOf<Int>()

    // --- theme

    @Test
    fun theme_lightAndDarkProvideTheirSchemesAndQuranColors() {
        var lightPrimary: Color? = null
        var lightGold: Color? = null
        var darkPrimary: Color? = null
        var darkGold: Color? = null
        var shape: Any? = null

        composeRule.setContent {
            Column {
                QuranVerseTheme(darkTheme = false) {
                    lightPrimary = MaterialTheme.colorScheme.primary
                    lightGold = MaterialTheme.quranColors.gold
                    shape = MaterialTheme.shapes.small
                }
                QuranVerseTheme(darkTheme = true) {
                    darkPrimary = MaterialTheme.colorScheme.primary
                    darkGold = MaterialTheme.quranColors.gold
                }
            }
        }
        composeRule.waitForIdle()

        assertEquals(LightColorScheme.primary, lightPrimary)
        assertEquals(LightQuranColors.gold, lightGold)
        assertEquals(AppShapes.small, shape)
        assertEquals(DarkColorScheme.primary, darkPrimary)
        assertEquals(DarkQuranColors.gold, darkGold)
    }

    @Test
    fun theme_usesAmiriForTitlesAndRobotoForBody() {
        var title: FontFamily? = null
        var body: FontFamily? = null

        composeRule.setContent {
            QuranVerseTheme {
                title = MaterialTheme.typography.titleLarge.fontFamily
                body = MaterialTheme.typography.bodyLarge.fontFamily
            }
        }
        composeRule.waitForIdle()

        assertEquals(AmiriFont, title)
        assertEquals(RobotoFont, body)
    }

    @Test
    fun typography_definesEveryStyleWithExpectedSizesAndFonts() {
        assertEquals(28.sp, AppTypography.arabicAyah().fontSize)
        assertEquals(56.sp, AppTypography.arabicAyah().lineHeight)
        assertEquals(AmiriFont, AppTypography.arabicAyah().fontFamily)
        assertEquals(26.sp, AppTypography.arabicTitle().fontSize)
        assertEquals(AmiriFont, AppTypography.headlineLargeEmphasized().fontFamily)
        assertEquals(AmiriFont, AppTypography.headlineMediumEmphasized().fontFamily)
        assertEquals(AmiriFont, AppTypography.headlineSmallEmphasized().fontFamily)
        assertEquals(AmiriFont, AppTypography.titleLargeEmphasized().fontFamily)
        assertEquals(AmiriFont, AppTypography.titleMediumEmphasized().fontFamily)
        assertEquals(FontWeight.SemiBold, AppTypography.titleSmallEmphasized().fontWeight)
        assertEquals(FontWeight.SemiBold, AppTypography.bodyLargeEmphasized().fontWeight)
        assertEquals(FontWeight.SemiBold, AppTypography.bodyMediumEmphasized().fontWeight)
        assertEquals(FontWeight.SemiBold, AppTypography.bodySmallEmphasized().fontWeight)
        assertEquals(16.sp, AppTypography.buttonLarge().fontSize)
        assertEquals(14.sp, AppTypography.buttonMedium().fontSize)
        assertEquals(12.sp, AppTypography.buttonSmall().fontSize)
        assertEquals(Color.Red, AppTypography.arabicAyah(color = Color.Red).color)

        val typography = AppTypography.toTypography()

        assertEquals(57.sp, typography.displayLarge.fontSize)
        assertEquals(45.sp, typography.displayMedium.fontSize)
        assertEquals(36.sp, typography.displaySmall.fontSize)
        assertEquals(32.sp, typography.headlineLarge.fontSize)
        assertEquals(28.sp, typography.headlineMedium.fontSize)
        assertEquals(24.sp, typography.headlineSmall.fontSize)
        assertEquals(24.sp, typography.titleLarge.fontSize)
        assertEquals(19.sp, typography.titleMedium.fontSize)
        assertEquals(14.sp, typography.titleSmall.fontSize)
        assertEquals(16.sp, typography.bodyLarge.fontSize)
        assertEquals(14.sp, typography.bodyMedium.fontSize)
        assertEquals(12.sp, typography.bodySmall.fontSize)
        assertEquals(14.sp, typography.labelLarge.fontSize)
        assertEquals(12.sp, typography.labelMedium.fontSize)
        assertEquals(11.sp, typography.labelSmall.fontSize)
    }

    // --- atoms

    @Test
    fun circleIconButton_plainAndGlassVariantsShowIconAndClick() {
        composeRule.setContent {
            QuranVerseTheme {
                Column {
                    CircleIconButton(
                        icon = Icons.Default.Search,
                        contentDescription = "Biasa",
                        onClick = { clickCount++ },
                    )
                    CircleIconButton(
                        icon = Icons.Default.Search,
                        contentDescription = "Kaca",
                        onClick = { secondCount++ },
                        glass = true,
                    )
                }
            }
        }

        composeRule.onNodeWithContentDescription("Biasa").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Biasa").performClick()
        composeRule.onNodeWithContentDescription("Kaca").performClick()

        assertEquals(1, clickCount)
        assertEquals(1, secondCount)
    }

    @Test
    fun circleIconButton_glassWithHolderStillClicks() {
        composeRule.setContent {
            QuranVerseTheme {
                CompositionLocalProvider(LocalGlassHolder provides rememberBlurHolder()) {
                    CircleIconButton(
                        icon = Icons.Default.Search,
                        contentDescription = "Kaca",
                        onClick = { clickCount++ },
                        glass = true,
                    )
                }
            }
        }

        composeRule.onNodeWithContentDescription("Kaca").performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun tonalIconButtonAndBackButton_handleClicks() {
        composeRule.setContent {
            QuranVerseTheme {
                Column {
                    TonalIconButton(
                        icon = Icons.Default.Search,
                        contentDescription = "Ubah",
                        onClick = { clickCount++ },
                        containerColor = Color.LightGray,
                        contentColor = Color.Black,
                    )
                    BackButton(onClick = { secondCount++ })
                }
            }
        }

        composeRule.onNodeWithContentDescription("Ubah").performClick()
        composeRule.onNodeWithContentDescription("Kembali").performClick()

        assertEquals(1, clickCount)
        assertEquals(1, secondCount)
    }

    @Test
    fun rosette_rendersWithoutCrashing() {
        composeRule.setContent {
            QuranVerseTheme {
                Rosette()
            }
        }

        composeRule.waitForIdle()
    }

    private fun lightStatusBars(): Boolean {
        val window = composeRule.activity.window

        return WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars
    }

    @Test
    fun statusBarIcons_lightBackgroundUsesDarkIcons() {
        composeRule.setContent {
            StatusBarIcons(background = Color.White)
        }
        composeRule.waitForIdle()

        assertEquals(true, lightStatusBars())
    }

    @Test
    fun statusBarIcons_darkBackgroundUsesLightIcons() {
        composeRule.setContent {
            StatusBarIcons(background = Color.Black)
        }
        composeRule.waitForIdle()

        assertEquals(false, lightStatusBars())
    }

    // --- molecules

    @Test
    fun numberBadgeAndNumberedRow_showContentSlotsAndClick() {
        composeRule.setContent {
            QuranVerseTheme {
                Column {
                    NumberBadge(text = "114")
                    NumberedRow(
                        number = "2",
                        title = "Al-Baqarah",
                        subtitle = "Sapi Betina",
                        onClick = { clickCount++ },
                        titleAccessory = { Text("(Madaniyah)") },
                        subtitleAccessory = { Text("286 ayat") },
                        detail = { Text("Detail tambahan") },
                        trailing = { Text("البقرة") },
                        showDivider = false,
                    )
                    NumberedRow(number = "3", title = "Ali Imran", onClick = {})
                }
            }
        }

        composeRule.onNodeWithText("114").assertIsDisplayed()
        composeRule.onNodeWithText("(Madaniyah)").assertIsDisplayed()
        composeRule.onNodeWithText("286 ayat").assertIsDisplayed()
        composeRule.onNodeWithText("Detail tambahan").assertIsDisplayed()
        composeRule.onNodeWithText("Ali Imran").assertIsDisplayed()

        composeRule.onNodeWithText("Al-Baqarah").performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun textInputField_showsLabelReportsTypingAndPlaceholder() {
        var typed = ""

        composeRule.setContent {
            QuranVerseTheme {
                TextInputField(
                    value = "",
                    onValueChange = { typed = it },
                    label = "Catatan",
                    placeholder = "Tambahkan catatan...",
                    singleLine = false,
                    minLines = 2,
                    maxLines = 4,
                )
            }
        }

        composeRule.onNodeWithText("Catatan").assertIsDisplayed()

        composeRule.onNode(hasSetTextAction()).performTextInput("Hafalan")

        assertEquals("Hafalan", typed)
    }

    @Test
    fun dropdownField_showsSelectedAndReportsChosenIndex() {
        composeRule.setContent {
            QuranVerseTheme {
                DropdownField(
                    label = "Kategori",
                    selected = "Hafalan",
                    options = listOf("Hafalan", "Murajaah"),
                    onSelected = { selectedIndexes += it },
                )
            }
        }

        composeRule.onNodeWithText("Kategori").assertIsDisplayed()

        composeRule.onNodeWithText("Hafalan").performClick()
        composeRule.onNodeWithText("Murajaah").performClick()

        assertEquals(listOf(1), selectedIndexes)
    }

    @Test
    fun readOnlyInfoCard_showsEveryLabelAndValue() {
        composeRule.setContent {
            QuranVerseTheme {
                ReadOnlyInfoCard(items = listOf("Surah" to "Al-Baqarah", "Ayat" to "255"))
            }
        }

        composeRule.onNodeWithText("Surah").assertIsDisplayed()
        composeRule.onNodeWithText("Al-Baqarah").assertIsDisplayed()
        composeRule.onNodeWithText("255").assertIsDisplayed()
    }

    @Test
    fun radioOptionRow_reflectsSelectionAndHandlesClick() {
        composeRule.setContent {
            QuranVerseTheme {
                var selected by remember { mutableStateOf("Terang") }

                Column {
                    RadioOptionRow(
                        label = "Terang",
                        selected = selected == "Terang",
                        onClick = { selected = "Terang" },
                    )
                    RadioOptionRow(
                        label = "Gelap",
                        selected = selected == "Gelap",
                        onClick = {
                            selected = "Gelap"
                            clickCount++
                        },
                    )
                }
            }
        }

        composeRule.onNodeWithText("Terang").assertIsSelected()

        composeRule.onNodeWithText("Gelap").performClick()

        composeRule.onNodeWithText("Gelap").assertIsSelected()
        assertEquals(1, clickCount)
    }

    @Test
    fun loadingAndMessageViews_showTheirContent() {
        composeRule.setContent {
            QuranVerseTheme {
                Column {
                    LoadingView(message = "Memuat data...")
                    LoadingView()
                    MessageView(message = "Belum ada kategori.")
                    MessageView(
                        message = "Gagal dimuat.",
                        actionLabel = "Coba Lagi",
                        onAction = { clickCount++ },
                        isError = true,
                    )
                }
            }
        }

        composeRule.onNodeWithText("Memuat data...").assertIsDisplayed()
        composeRule.onNodeWithText("Belum ada kategori.").assertIsDisplayed()

        composeRule.onNodeWithText("Coba Lagi").performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun settingItem_showsTitleSubtitleAndClick() {
        composeRule.setContent {
            QuranVerseTheme {
                SettingItem(
                    title = "Sumber data",
                    subtitle = "Asal teks dan terjemahan",
                    onClick = { clickCount++ },
                )
            }
        }

        composeRule.onNodeWithText("Asal teks dan terjemahan").assertIsDisplayed()

        composeRule.onNodeWithText("Sumber data").performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun underlineTabRow_reportsClickedIndexInOrder() {
        composeRule.setContent {
            QuranVerseTheme {
                var selected by remember { mutableIntStateOf(0) }

                UnderlineTabRow(
                    titles = listOf("Surah", "Juz"),
                    selectedIndex = selected,
                    onTabSelected = {
                        selected = it
                        selectedIndexes += it
                    },
                )
            }
        }

        composeRule.onNodeWithText("Juz").performClick()
        composeRule.onNodeWithText("Surah").performClick()

        assertEquals(listOf(1, 0), selectedIndexes)
    }

    // --- organisms

    @Test
    fun appBar_showsTitleSubtitleSlotsAndHidesBlankSubtitle() {
        composeRule.setContent {
            QuranVerseTheme {
                Column {
                    AppBar(
                        title = "Kelola Kategori",
                        subtitle = "Hafalan",
                        background = AppBarBackground.Primary,
                        navigationIcon = { Text("Nav") },
                        actions = { Text("Aksi") },
                    )
                    AppBar(title = "Pengaturan", subtitle = "   ")
                }
            }
        }

        composeRule.onNodeWithText("Kelola Kategori").assertIsDisplayed()
        composeRule.onNodeWithText("Hafalan").assertIsDisplayed()
        composeRule.onNodeWithText("Nav").assertIsDisplayed()
        composeRule.onNodeWithText("Aksi").assertIsDisplayed()
        composeRule.onNodeWithText("Pengaturan").assertIsDisplayed()
    }

    @Test
    fun quranVerseAppBar_showsBrandButtonsAndExtraActions() {
        composeRule.setContent {
            QuranVerseTheme {
                QuranVerseAppBar(
                    onSearchClick = { clickCount++ },
                    onSettingsClick = { secondCount++ },
                    extraActions = { Text("Ekstra") },
                )
            }
        }

        composeRule.onNodeWithText("Quran Verse").assertIsDisplayed()
        composeRule.onNodeWithText("Temukan surah dan juz untuk mulai membaca")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Ekstra").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Cari").performClick()
        composeRule.onNodeWithContentDescription("Pengaturan").performClick()

        assertEquals(1, clickCount)
        assertEquals(1, secondCount)
    }

    @Test
    fun quranVerseAppBar_nullSubtitleHidesIt() {
        composeRule.setContent {
            QuranVerseTheme {
                QuranVerseAppBar(subtitle = null)
            }
        }

        composeRule.onNodeWithText("Temukan surah dan juz untuk mulai membaca")
            .assertDoesNotExist()
    }

    @Test
    fun appDialog_showsContentAndForwardsConfirmAndDismiss() {
        composeRule.setContent {
            QuranVerseTheme {
                AppDialog(
                    title = "Judul dialog",
                    subtitle = "Subjudul dialog",
                    onDismiss = { secondCount++ },
                    confirmText = "Simpan",
                    onConfirm = { clickCount++ },
                    icon = Icons.Default.Search,
                    destructive = true,
                ) {
                    Text("Isi dialog")
                }
            }
        }

        composeRule.onNodeWithText("Judul dialog").assertIsDisplayed()
        composeRule.onNodeWithText("Subjudul dialog").assertIsDisplayed()
        composeRule.onNodeWithText("Isi dialog").assertIsDisplayed()

        composeRule.onNodeWithText("Simpan").performClick()
        composeRule.onNodeWithText("Batal").performClick()

        assertEquals(1, clickCount)
        assertEquals(1, secondCount)
    }

    @Test
    fun appDialog_withoutConfirmShowsOnlyCustomDismiss() {
        composeRule.setContent {
            QuranVerseTheme {
                AppDialog(
                    title = "Info",
                    onDismiss = { clickCount++ },
                    dismissText = "Tutup",
                ) {
                    Text("Isi")
                }
            }
        }

        composeRule.onNodeWithText("Tutup").performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun appDialog_disabledConfirmCannotBeUsed() {
        composeRule.setContent {
            QuranVerseTheme {
                AppDialog(
                    title = "Form",
                    onDismiss = {},
                    confirmText = "Simpan",
                    confirmEnabled = false,
                ) {
                    Text("Isi")
                }
            }
        }

        composeRule.onNodeWithText("Simpan").assertIsNotEnabled()
    }

    @Test
    fun appDialog_enabledConfirmIsEnabled() {
        composeRule.setContent {
            QuranVerseTheme {
                AppDialog(
                    title = "Form",
                    onDismiss = {},
                    confirmText = "Simpan",
                ) {
                    Text("Isi")
                }
            }
        }

        composeRule.onNodeWithText("Simpan").assertIsEnabled()
    }

    // --- glass

    @Test
    fun glassSurface_rendersContentOverBlurredBackdrop() {
        composeRule.setContent {
            QuranVerseTheme {
                val holder = rememberBlurHolder()

                Box(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize().blurredContent(holder))

                    Box(
                        modifier = Modifier.glassSurface(
                            holder = holder,
                            shape = CircleShape,
                            tint = Color.White,
                            borderColor = Color.Gray,
                        ),
                    ) {
                        Text("Di atas kaca")
                    }
                }
            }
        }

        composeRule.onNodeWithText("Di atas kaca").assertIsDisplayed()
    }
}
