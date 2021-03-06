package com.marcellogalhardo.fixture.internal.resolver

import com.google.common.truth.Truth.assertThat
import com.marcellogalhardo.fixture.FixtureContext
import com.marcellogalhardo.fixture.internal.resolver.SimpleResolver
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class SimpleResolverTest {

    val anyType = FixtureContext.Type::class.simpleName
    val anyParam = FixtureContext.Param::class.simpleName

    private lateinit var sut: SimpleResolver

    @Before
    fun setup() {
        sut = object : SimpleResolver() {
            override fun resolveType(context: FixtureContext.Type) = anyType
            override fun resolveParam(context: FixtureContext.Param) = anyParam
        }
    }

    @Test
    fun resolve_shouldInvokeResolveType_givenTypeContext() {
        val context = mockk<FixtureContext.Type>()

        val result = sut.resolve(context)

        assertThat(result).isEqualTo(anyType)
    }

    @Test
    fun resolve_shouldInvokeResolveType_givenParamContext() {
        val context = mockk<FixtureContext.Param>()

        val result = sut.resolve(context)

        assertThat(result).isEqualTo(anyParam)
    }
}