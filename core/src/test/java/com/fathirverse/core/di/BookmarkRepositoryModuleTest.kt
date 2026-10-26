package com.fathirverse.core.di

import com.fathirverse.core.data.repositories.BookmarkCategoryRepositoryImpl
import com.fathirverse.core.data.repositories.BookmarkRepositoryImpl
import com.fathirverse.core.domain.repository.BookmarkCategoryRepository
import com.fathirverse.core.domain.repository.BookmarkRepository
import dagger.Binds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BookmarkRepositoryModuleTest {

    @Test
    fun bindsBookmarkRepositoryImplAsBookmarkRepository() {
        val method = BookmarkRepositoryModule::class.java.getMethod(
            "bindBookmarkRepository",
            BookmarkRepositoryImpl::class.java,
        )

        assertEquals(BookmarkRepository::class.java, method.returnType)
        assertNotNull(method.getAnnotation(Binds::class.java))
    }

    @Test
    fun bindsBookmarkCategoryRepositoryImplAsBookmarkCategoryRepository() {
        val method = BookmarkRepositoryModule::class.java.getMethod(
            "bindBookmarkCategoryRepository",
            BookmarkCategoryRepositoryImpl::class.java,
        )

        assertEquals(BookmarkCategoryRepository::class.java, method.returnType)
        assertNotNull(method.getAnnotation(Binds::class.java))
    }
}
