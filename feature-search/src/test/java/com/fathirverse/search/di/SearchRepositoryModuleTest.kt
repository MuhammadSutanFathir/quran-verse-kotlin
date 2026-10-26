package com.fathirverse.search.di

import com.fathirverse.search.data.repositories.SearchRepositoryImpl
import com.fathirverse.search.domain.repository.SearchRepository
import dagger.Binds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SearchRepositoryModuleTest {

    @Test
    fun bindsSearchRepositoryImplAsSearchRepository() {
        val method = SearchRepositoryModule::class.java.getMethod(
            "bindSearchRepository",
            SearchRepositoryImpl::class.java,
        )

        assertEquals(SearchRepository::class.java, method.returnType)
        assertNotNull(method.getAnnotation(Binds::class.java))
    }
}
