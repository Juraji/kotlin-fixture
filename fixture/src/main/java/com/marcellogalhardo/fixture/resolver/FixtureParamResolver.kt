package com.marcellogalhardo.fixture.resolver

import com.marcellogalhardo.fixture.NextFunction
import com.marcellogalhardo.fixture.resolver.param.ClassParamResolver
import com.marcellogalhardo.fixture.resolver.param.CompositeParamResolver
import com.marcellogalhardo.fixture.resolver.param.TypeParamResolver
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface FixtureParamResolver {

    fun resolve(classRef: KClass<*>, classType: KType, paramType: KType): Any?
}

@Suppress("FunctionName")
fun FixtureParamResolver(nextFunction: NextFunction): FixtureParamResolver {
    val classParamResolver = ClassParamResolver(nextFunction)
    val typeParamResolver = TypeParamResolver(nextFunction)
    return CompositeParamResolver(classParamResolver, typeParamResolver)
}