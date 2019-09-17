package com.marcellogalhardo.fixture.resolver.type

import com.marcellogalhardo.fixture.FixtureRandom
import com.marcellogalhardo.fixture.resolver.FixtureTypeResolver
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal class StandardTypeResolver(
    private val random: FixtureRandom
) : FixtureTypeResolver {

    override fun resolve(classRef: KClass<*>, typeRef: KType): Any? = random.run {
        return when (classRef) {
            Boolean::class -> nextBoolean()
            Char::class -> nextChar()
            Double::class -> nextDouble()
            Float::class -> nextFloat()
            Int::class -> nextInt()
            Long::class -> nextLong()
            String::class -> nextString()
            else -> null
        }
    }
}