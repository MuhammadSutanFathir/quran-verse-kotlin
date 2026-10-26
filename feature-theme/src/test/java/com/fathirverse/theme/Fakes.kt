package com.fathirverse.theme

import android.content.SharedPreferences
import arrow.core.Either
import com.fathirverse.theme.domain.entities.ThemeMode
import com.fathirverse.theme.domain.failure.ThemeFailure
import com.fathirverse.theme.domain.repository.ThemeRepository
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

class FakeThemeRepository : ThemeRepository {

    var mode: Either<ThemeFailure, ThemeMode> = Either.Right(ThemeMode.SYSTEM)
    var saveResult: Either<ThemeFailure, Unit> = Either.Right(Unit)

    var savedMode: ThemeMode? = null
        private set

    override suspend fun getThemeMode(): Either<ThemeFailure, ThemeMode> = mode

    override suspend fun setThemeMode(mode: ThemeMode): Either<ThemeFailure, Unit> {
        savedMode = mode
        return saveResult
    }
}

class ThrowingPreferences(
    private val failure: Throwable,
) : SharedPreferences {

    override fun getAll(): MutableMap<String, *> = throw failure

    override fun getString(key: String?, defValue: String?): String? = throw failure

    override fun getStringSet(
        key: String?,
        defValues: MutableSet<String>?,
    ): MutableSet<String>? = throw failure

    override fun getInt(key: String?, defValue: Int): Int = throw failure

    override fun getLong(key: String?, defValue: Long): Long = throw failure

    override fun getFloat(key: String?, defValue: Float): Float = throw failure

    override fun getBoolean(key: String?, defValue: Boolean): Boolean = throw failure

    override fun contains(key: String?): Boolean = throw failure

    override fun edit(): SharedPreferences.Editor = throw failure

    override fun registerOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?,
    ) = Unit

    override fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?,
    ) = Unit
}
