package com.marcellogalhardo.fixture.internal

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.FixtureConfigs
import com.marcellogalhardo.fixture.FixtureRandom
import com.marcellogalhardo.fixture.FixtureResolver
import com.marcellogalhardo.fixture.TypeNotSupported
import com.marcellogalhardo.fixture.fixtureRandomOf
import com.marcellogalhardo.fixture.internal.resolver.composite.CustomTypeResolver
import com.marcellogalhardo.fixture.internal.resolver.composite.DefaultTypeResolver
import com.marcellogalhardo.fixture.resolve
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal class DefaultFixture constructor(
    configs: FixtureConfigs,
    private val random: FixtureRandom = fixtureRandomOf()
) : Fixture, FixtureRandom by random {

    private val customTypeResolver = CustomTypeResolver()

    private val defaultTypeResolver = DefaultTypeResolver(configs)

    override fun <T : Any> register(classRef: KClass<T>, resolver: FixtureResolver) {
        customTypeResolver[classRef] = resolver
    }

    override fun create(classRef: KClass<*>, classType: KType): Any? {
        return customTypeResolver[classRef]?.resolve(this, classRef, classType)
            ?: defaultTypeResolver.resolve(this, classRef, classType)
            ?: throw TypeNotSupported(classRef)
    }
}