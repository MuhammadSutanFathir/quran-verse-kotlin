package com.fathirverse.quran

import arrow.core.Either
import com.fathirverse.core.data.datasources.local.QuranDao
import com.fathirverse.core.data.models.JuzModel
import com.fathirverse.core.data.models.SurahModel
import com.fathirverse.core.data.models.VerseModel
import com.fathirverse.core.data.models.VerseSearchGroupModel
import com.fathirverse.core.data.models.VerseSearchModel
import com.fathirverse.core.domain.entities.Bookmark
import com.fathirverse.core.domain.entities.BookmarkCategory
import com.fathirverse.core.domain.entities.BookmarkReferenceType
import com.fathirverse.core.domain.entities.BookmarkSummary
import com.fathirverse.core.domain.repository.BookmarkCategoryRepository
import com.fathirverse.core.domain.repository.BookmarkRepository
import com.fathirverse.core.failure.BookmarkFailure
import com.fathirverse.quran.domain.entities.Juz
import com.fathirverse.quran.domain.entities.Surah
import com.fathirverse.quran.domain.entities.Verse
import com.fathirverse.quran.domain.failure.QuranFailure
import com.fathirverse.quran.domain.repository.QuranRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

class FakeQuranRepository : QuranRepository {

    var surahs: Either<QuranFailure, List<Surah>> = Either.Right(emptyList())
    var juzs: Either<QuranFailure, List<Juz>> = Either.Right(emptyList())
    var versesBySurah: Either<QuranFailure, List<Verse>> = Either.Right(emptyList())
    var versesByJuz: Either<QuranFailure, List<Verse>> = Either.Right(emptyList())

    var versesBySurahCalls = 0
        private set

    var versesByJuzCalls = 0
        private set

    var lastSurahNumber: Int? = null
        private set

    var lastJuzNumber: Int? = null
        private set

    override suspend fun getSurahs(): Either<QuranFailure, List<Surah>> = surahs

    override suspend fun getJuzs(): Either<QuranFailure, List<Juz>> = juzs

    override suspend fun getVersesBySurah(
        surahNumber: Int,
    ): Either<QuranFailure, List<Verse>> {
        versesBySurahCalls++
        lastSurahNumber = surahNumber
        return versesBySurah
    }

    override suspend fun getVersesByJuz(
        juzNumber: Int,
    ): Either<QuranFailure, List<Verse>> {
        versesByJuzCalls++
        lastJuzNumber = juzNumber
        return versesByJuz
    }
}

class FakeQuranDao : QuranDao {

    var failure: Throwable? = null

    var surahs: List<SurahModel> = emptyList()
    var juzs: List<JuzModel> = emptyList()
    var versesBySurah: List<VerseModel> = emptyList()
    var versesByJuz: List<VerseModel> = emptyList()

    var lastSurahNumber: Int? = null
        private set

    var lastJuzNumber: Int? = null
        private set

    private fun throwIfFailing() {
        failure?.let { throw it }
    }

    override suspend fun getSurahs(): List<SurahModel> {
        throwIfFailing()
        return surahs
    }

    override suspend fun getJuzs(): List<JuzModel> {
        throwIfFailing()
        return juzs
    }

    override suspend fun getVersesBySurah(surahNumber: Int): List<VerseModel> {
        throwIfFailing()
        lastSurahNumber = surahNumber
        return versesBySurah
    }

    override suspend fun getVersesByJuz(juzNumber: Int): List<VerseModel> {
        throwIfFailing()
        lastJuzNumber = juzNumber
        return versesByJuz
    }

    override suspend fun searchVerseGroups(pattern: String): List<VerseSearchGroupModel> {
        return emptyList()
    }

    override suspend fun searchVersesInSurah(
        surahNumber: Int,
        pattern: String,
    ): List<VerseSearchModel> = emptyList()
}

class FakeBookmarkRepository : BookmarkRepository {

    var latest: Either<BookmarkFailure, Bookmark?> = Either.Right(null)
    var byReference: Either<BookmarkFailure, List<Bookmark>> = Either.Right(emptyList())
    var addResult: Either<BookmarkFailure, Unit> = Either.Right(Unit)

    var addedBookmark: Bookmark? = null
        private set

    var byReferenceArgs: Triple<BookmarkReferenceType, Int, Long?>? = null
        private set

    override suspend fun getLatestBookmark(): Either<BookmarkFailure, Bookmark?> = latest

    override suspend fun getBookmarkSummaries(
        type: BookmarkReferenceType,
        categoryId: Long?,
    ): Either<BookmarkFailure, List<BookmarkSummary>> = Either.Right(emptyList())

    override suspend fun getBookmarksByReference(
        type: BookmarkReferenceType,
        referenceNumber: Int,
        categoryId: Long?,
    ): Either<BookmarkFailure, List<Bookmark>> {
        byReferenceArgs = Triple(type, referenceNumber, categoryId)
        return byReference
    }

    override suspend fun addBookmark(bookmark: Bookmark): Either<BookmarkFailure, Unit> {
        addedBookmark = bookmark
        return addResult
    }

    override suspend fun deleteBookmark(bookmarkId: Long): Either<BookmarkFailure, Unit> {
        return Either.Right(Unit)
    }
}

class FakeBookmarkCategoryRepository : BookmarkCategoryRepository {

    var categories: Either<BookmarkFailure, List<BookmarkCategory>> = Either.Right(emptyList())

    override suspend fun getCategories(): Either<BookmarkFailure, List<BookmarkCategory>> {
        return categories
    }

    override suspend fun addCategory(name: String): Either<BookmarkFailure, Unit> {
        return Either.Right(Unit)
    }

    override suspend fun renameCategory(
        categoryId: Long,
        name: String,
    ): Either<BookmarkFailure, Unit> = Either.Right(Unit)

    override suspend fun deleteCategory(categoryId: Long): Either<BookmarkFailure, Unit> {
        return Either.Right(Unit)
    }
}

fun surahModel(number: Int = 1) = SurahModel(
    number = number,
    nameLatin = "Al-Fatihah",
    nameId = "Pembukaan",
    nameArabic = "الفاتحة",
    revelation = "Makkiyah",
    verseCount = 7,
)

fun juzModel(number: Int = 1) = JuzModel(
    number = number,
    startSurah = 1,
    startVerse = 1,
    endSurah = 2,
    endVerse = 141,
    startSurahName = "Al-Fatihah",
    endSurahName = "Al-Baqarah",
)

fun verseModel(
    surah: Int = 1,
    verse: Int = 1,
    juz: Int = 1,
) = VerseModel(
    surahNumber = surah,
    verseNumber = verse,
    juzNumber = juz,
    arabic = "بِسْمِ",
    transliteration = "Bismillāh",
    translationId = "Dengan nama Allah.",
)

fun surah(number: Int = 1, name: String = "Al-Fatihah") = Surah(
    number = number,
    nameLatin = name,
    nameId = "Pembukaan",
    nameArabic = "الفاتحة",
    revelation = "Makkiyah",
    verseCount = 7,
)

fun juz(number: Int = 1) = Juz(
    number = number,
    startSurah = 1,
    startVerse = 1,
    endSurah = 2,
    endVerse = 141,
    startSurahName = "Al-Fatihah",
    endSurahName = "Al-Baqarah",
)

fun verse(
    surah: Int = 1,
    verse: Int = 1,
    juz: Int = 1,
    translation: String = "Dengan nama Allah.",
) = Verse(
    surahNumber = surah,
    verseNumber = verse,
    juzNumber = juz,
    arabic = "بِسْمِ ٱللَّهِ",
    transliteration = "Bismillāh $surah:$verse",
    translationId = translation,
)

fun bookmark(
    id: Long = 1,
    categoryId: Long = 1,
    type: String = "SURAH",
    surah: Int = 1,
    verse: Int = 1,
    juz: Int = 1,
    surahName: String = "Al-Fatihah",
) = Bookmark(
    id = id,
    categoryId = categoryId,
    type = type,
    surahNumber = surah,
    verseNumber = verse,
    juzNumber = juz,
    note = null,
    createdAt = "2026-10-25 08:30:00",
    surahName = surahName,
)
