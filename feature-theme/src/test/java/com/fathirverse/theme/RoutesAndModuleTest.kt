package com.fathirverse.theme

import com.fathirverse.core.exception.AppException
import com.fathirverse.theme.data.exception.ThemeException
import com.fathirverse.theme.data.mappers.toThemeMode
import com.fathirverse.theme.data.repositories.ThemeRepositoryImpl
import com.fathirverse.theme.di.ThemeRepositoryModule
import com.fathirverse.theme.domain.entities.ThemeMode
import com.fathirverse.theme.domain.repository.ThemeRepository
import com.fathirverse.theme.router.SettingsRoute
import dagger.Binds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class RoutesAndModuleTest {

    @Test
    fun settingsRoutes_haveStableDistinctNames() {
        assertEquals("settings_home", SettingsRoute.HOME)
        assertEquals("settings_data_source", SettingsRoute.DATA_SOURCE)
        assertNotEquals(SettingsRoute.HOME, SettingsRoute.DATA_SOURCE)
    }

    @Test
    fun bindsThemeRepositoryImplAsThemeRepository() {
        val method = ThemeRepositoryModule::class.java.getMethod(
            "bindThemeRepository",
            ThemeRepositoryImpl::class.java,
        )

        assertEquals(ThemeRepository::class.java, method.returnType)
        assertNotNull(method.getAnnotation(Binds::class.java))
    }

    @Test
    fun toThemeMode_knownNames_mapToTheirMode() {
        assertEquals(ThemeMode.LIGHT, "LIGHT".toThemeMode())
        assertEquals(ThemeMode.DARK, "DARK".toThemeMode())
        assertEquals(ThemeMode.SYSTEM, "SYSTEM".toThemeMode())
    }

    @Test
    fun toThemeMode_nullUnknownOrWrongCase_fallsBackToSystem() {
        assertEquals(ThemeMode.SYSTEM, (null as String?).toThemeMode())
        assertEquals(ThemeMode.SYSTEM, "".toThemeMode())
        assertEquals(ThemeMode.SYSTEM, "SEPIA".toThemeMode())
        assertEquals(ThemeMode.SYSTEM, "dark".toThemeMode())
    }

    @Test
    fun themeException_keepsCauseAndIsAnAppException() {
        val cause = IllegalStateException("prefs error")
        val exception: Exception = ThemeException(cause)

        assertSame(cause, exception.cause)
        assertTrue(exception is AppException)
        assertNull(ThemeException().cause)
    }
}
