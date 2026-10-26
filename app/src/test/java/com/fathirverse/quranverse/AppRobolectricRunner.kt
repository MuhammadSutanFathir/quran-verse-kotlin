package com.fathirverse.quranverse

import org.junit.runners.model.FrameworkMethod
import org.robolectric.RobolectricTestRunner
import org.robolectric.internal.bytecode.InstrumentationConfiguration

class AppRobolectricRunner(testClass: Class<*>) : RobolectricTestRunner(testClass) {

    override fun createClassLoaderConfig(method: FrameworkMethod): InstrumentationConfiguration {
        return InstrumentationConfiguration.Builder(super.createClassLoaderConfig(method))
            .doNotInstrumentClass("com.fathirverse.quranverse.MainActivity")
            .doNotInstrumentClass("com.fathirverse.quranverse.QuranVerseApplication")
            .build()
    }
}
