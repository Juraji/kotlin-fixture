package com.marcellogalhardo.fixture.resolver.type

import com.marcellogalhardo.fixture.FixtureContext
import com.marcellogalhardo.fixture.FixtureRandom
import com.marcellogalhardo.fixture.FixtureResolver

internal class BooleanTypeResolver(
    private val random: FixtureRandom
) : FixtureResolver.Type {

    override fun resolveType(context: FixtureContext.Type): Any? = context.run {
        return when (classRef) {
            Boolean::class -> random.nextBoolean()
            else -> null
        }
    }
}