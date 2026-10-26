package com.fathirverse.theme.data

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import arrow.core.Either
import com.fathirverse.theme.ThrowingPreferences
import com.fathirverse.theme.data.datasources.local.ThemeLocalDataSource
import com.fathirverse.theme.data.exception.ThemeException
import com.fathirverse.theme.data.repositories.ThemeRepositoryImpl
import com.fathirverse.theme.domain.entities.ThemeMode
import com.fathirverse.theme.domain.failure.ThemeFailure
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ThemeStorageTest {

    private lateinit var context: Context

    private val failure = IllegalStateException("prefs error")

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()

        context.getSharedPreferences("theme_preferences", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    private fun failingContext(): Context {
        return object : ContextWrapper(context) {
            override fun getSharedPreferences(name: String?, mode: Int): SharedPreferences {
                return ThrowingPreferences(failure)
            }
        }
    }

    @Test
    fun dataSource_nothingSaved_returnsNull() {
        assertNull(ThemeLocalDataSource(context).getThemeMode())
    }

    @Test
    fun dataSource_savedMode_isReadBackAndOverwritten() {
        val dataSource = ThemeLocalDataSource(context)

        dataSource.saveThemeMode("DARK")
        assertEquals("DARK", dataSource.getThemeMode())

        dataSource.saveThemeMode("LIGHT")
        assertEquals("LIGHT", ThemeLocalDataSource(context).getThemeMode())
    }

    @Test
    fun dataSource_readFailure_isWrappedInThemeException() {
        try {
            ThemeLocalDataSource(failingContext()).getThemeMode()
            fail("ThemeException seharusnya dilempar")
        } catch (exception: ThemeException) {
            assertSame(failure, exception.cause)
        }
    }

    @Test
    fun dataSource_writeFailure_isWrappedInThemeException() {
        try {
            ThemeLocalDataSource(failingContext()).saveThemeMode("DARK")
            fail("ThemeException seharusnya dilempar")
        } catch (exception: ThemeException) {
            assertSame(failure, exception.cause)
        }
    }

    @Test
    fun repository_nothingSaved_defaultsToSystem() = runTest {
        val repository = ThemeRepositoryImpl(ThemeLocalDataSource(context))

        assertEquals(Either.Right(ThemeMode.SYSTEM), repository.getThemeMode())
    }

    @Test
    fun repository_setThenGet_roundTripsEveryMode() = runTest {
        val repository = ThemeRepositoryImpl(ThemeLocalDataSource(context))

        ThemeMode.entries.forEach { mode ->
            assertEquals(Either.Right(Unit), repository.setThemeMode(mode))
            assertEquals(Either.Right(mode), repository.getThemeMode())
        }
    }

    @Test
    fun repository_readFailure_returnsLoadFailed() = runTest {
        val repository = ThemeRepositoryImpl(ThemeLocalDataSource(failingContext()))

        assertEquals(Either.Left(ThemeFailure.LoadFailed), repository.getThemeMode())
    }

    @Test
    fun repository_writeFailure_returnsSaveFailed() = runTest {
        val repository = ThemeRepositoryImpl(ThemeLocalDataSource(failingContext()))

        assertEquals(
            Either.Left(ThemeFailure.SaveFailed),
            repository.setThemeMode(ThemeMode.DARK),
        )
    }
}
