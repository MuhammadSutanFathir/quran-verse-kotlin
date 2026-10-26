
package com.fathirverse.quranverse

import android.app.Application
import androidx.compose.ui.text.font.createFontFamilyResolver
import com.fathirverse.uikit.theme.AmiriFont
import com.fathirverse.uikit.theme.RobotoFont
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class QuranVerseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            val resolver = createFontFamilyResolver(this@QuranVerseApplication)
            resolver.preload(AmiriFont)
            resolver.preload(RobotoFont)
        }
    }
}
