package com.marcellogalhardo.fixture.resolver.param

import com.marcellogalhardo.fixture.FixtureBuilder
import com.marcellogalhardo.fixture.FixtureContext
import com.marcellogalhardo.fixture.FixtureResolver
import kotlin.reflect.KClass

class ClassParamResolver(
    private val builder: FixtureBuilder
) : FixtureResolver.Param {
    override fun resolveParam(context: FixtureContext.Param): Any? = context.run {
        return when (val classifier = paramType.classifier) {
            is KClass<*> -> builder.next(classifier, context.paramType)
            else -> null
        }
    }
}