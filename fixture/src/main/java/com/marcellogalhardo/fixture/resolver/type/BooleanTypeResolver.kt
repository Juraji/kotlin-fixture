package com.marcellogalhardo.fixture.resolver.type

import com.marcellogalhardo.fixture.FixtureRandom
import com.marcellogalhardo.fixture.resolver.FixtureTypeResolver
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal class BooleanTypeResolver(
    private val random: FixtureRandom
) : FixtureTypeResolver {

    override fun resolve(classRef: KClass<*>, typeRef: KType): Any? = random.run {
        return when (classRef) {
            Boolean::class -> nextBoolean()
            else -> null
        }
    }
}