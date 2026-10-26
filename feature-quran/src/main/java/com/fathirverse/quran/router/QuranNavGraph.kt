package com.fathirverse.quran.router

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.quran.domain.entities.Juz
import com.fathirverse.quran.domain.entities.Surah
import com.fathirverse.quran.presentation.screens.HomeScreen
import com.fathirverse.quran.presentation.screens.JuzDetailScreen
import com.fathirverse.quran.presentation.screens.SurahDetailScreen

object QuranRoute {
    const val GRAPH = "quran_graph"
    const val HOME = "quran_home"

    private const val ARG_NUMBER = "number"
    private const val ARG_SURAH = "surah"
    private const val ARG_AYAH = "ayah"

    private const val NO_VALUE = -1

    const val SURAH = "quran_surah/{$ARG_NUMBER}?$ARG_AYAH={$ARG_AYAH}"
    const val JUZ = "quran_juz/{$ARG_NUMBER}?$ARG_SURAH={$ARG_SURAH}&$ARG_AYAH={$ARG_AYAH}"

    fun surah(
        number: Int,
        ayah: Int? = null,
    ): String {
        return if (ayah != null) {
            "quran_surah/$number?$ARG_AYAH=$ayah"
        } else {
            "quran_surah/$number"
        }
    }

    fun juz(
        number: Int,
        surah: Int? = null,
        ayah: Int? = null,
    ): String {
        return if (surah != null && ayah != null) {
            "quran_juz/$number?$ARG_SURAH=$surah&$ARG_AYAH=$ayah"
        } else {
            "quran_juz/$number"
        }
    }

    fun bookmark(bookmark: Bookmark): String {
        return if (bookmark.type == BookmarkReferenceType.JUZ.name) {
            juz(
                number = bookmark.juzNumber,
                surah = bookmark.surahNumber,
                ayah = bookmark.verseNumber,
            )
        } else {
            surah(
                number = bookmark.surahNumber,
                ayah = bookmark.verseNumber,
            )
        }
    }

    internal fun numberArg() = ARG_NUMBER
    internal fun surahArg() = ARG_SURAH
    internal fun ayahArg() = ARG_AYAH
    internal fun noValue() = NO_VALUE
}

fun NavGraphBuilder.quranGraph(
    navController: NavHostController,
    onManageBookmarkCategories: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
) {
    val noValue = QuranRoute.noValue()

    navigation(
        route = QuranRoute.GRAPH,
        startDestination = QuranRoute.HOME,
    ) {
        composable(QuranRoute.HOME) {
            HomeScreen(
                onSurahClick = { surah: Surah ->
                    navController.navigate(
                        QuranRoute.surah(surah.number),
                    )
                },
                onJuzClick = { juz: Juz ->
                    navController.navigate(
                        QuranRoute.juz(juz.number),
                    )
                },
                onLastReadClick = { bookmark ->
                    navController.navigate(
                        QuranRoute.bookmark(bookmark),
                    )
                },
                onSettingsClick = onSettingsClick,
                onSearchClick = onSearchClick,
            )
        }

        composable(
            route = QuranRoute.SURAH,
            arguments = listOf(
                navArgument(QuranRoute.numberArg()) {
                    type = NavType.IntType
                },
                navArgument(QuranRoute.ayahArg()) {
                    type = NavType.IntType
                    defaultValue = noValue
                },
            ),
        ) { backStackEntry ->
            val number = backStackEntry.arguments?.getInt(QuranRoute.numberArg())
                ?: return@composable

            val ayah = backStackEntry.arguments
                ?.getInt(QuranRoute.ayahArg(), noValue)
                ?.takeIf { it != noValue }

            SurahDetailScreen(
                number = number,
                initialAyah = ayah,
                onBackClick = {
                    navController.popBackStack()
                },
                onManageBookmarkCategories = onManageBookmarkCategories,
            )
        }

        composable(
            route = QuranRoute.JUZ,
            arguments = listOf(
                navArgument(QuranRoute.numberArg()) {
                    type = NavType.IntType
                },
                navArgument(QuranRoute.surahArg()) {
                    type = NavType.IntType
                    defaultValue = noValue
                },
                navArgument(QuranRoute.ayahArg()) {
                    type = NavType.IntType
                    defaultValue = noValue
                },
            ),
        ) { backStackEntry ->
            val number = backStackEntry.arguments?.getInt(QuranRoute.numberArg())
                ?: return@composable

            val surah = backStackEntry.arguments
                ?.getInt(QuranRoute.surahArg(), noValue)
                ?.takeIf { it != noValue }

            val ayah = backStackEntry.arguments
                ?.getInt(QuranRoute.ayahArg(), noValue)
                ?.takeIf { it != noValue }

            JuzDetailScreen(
                number = number,
                initialVerse = if (surah != null && ayah != null) {
                    surah to ayah
                } else {
                    null
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onManageBookmarkCategories = onManageBookmarkCategories,
            )
        }
    }
}
