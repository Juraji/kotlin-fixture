package com.marcellogalhardo.fixture.internal

import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.full.createType

/**
 * Returns KType of an Reified.
 * This code was taken from https://gist.github.com/udalov/bb6f398c2e643ee69586356fdd67e9b1
 */
inline fun <reified T : Any> getKType(): KType =
    object : SuperTypeTokenHolder<T>() {}.getKTypeImpl()

open class SuperTypeTokenHolder<T>

fun SuperTypeTokenHolder<*>.getKTypeImpl(): KType = javaClass.genericSuperclass
    .toKType()
    .arguments
    .single()
    .type!!

private fun KClass<*>.toInvariantFlexibleProjection(
    arguments: List<KTypeProjection> = emptyList()
): KTypeProjection {
    val args = if (java.isArray) {
        listOf(java.componentType.kotlin.toInvariantFlexibleProjection())
    } else {
        arguments
    }
    return KTypeProjection.invariant(createType(args, nullable = false))
}

private fun Type.toKTypeProjection(): KTypeProjection = when (this) {
    is Class<*> -> this.kotlin.toInvariantFlexibleProjection()
    is ParameterizedType -> {
        val erasure = (rawType as Class<*>).kotlin
        erasure.toInvariantFlexibleProjection(
            (erasure.typeParameters.zip(actualTypeArguments)
                .map { (parameter, argument) ->
                    val projection = argument.toKTypeProjection()
                    projection.takeIf {
                        // Get rid of use-site projections on arguments, where the corresponding
                        // parameters already have a declaration-site projection
                        parameter.variance == KVariance.INVARIANT || parameter.variance != projection.variance
                    } ?: KTypeProjection.invariant(projection.type!!)
                })
        )
    }
    is WildcardType -> when {
        lowerBounds.isNotEmpty() -> KTypeProjection.contravariant(lowerBounds.single().toKType())
        upperBounds.isNotEmpty() -> KTypeProjection.covariant(upperBounds.single().toKType())
        // This looks impossible to obtain through Java reflection API, but someone may construct
        // and pass such an instance here anyway
        else -> KTypeProjection.STAR
    }
    is GenericArrayType -> Array<Any>::class.toInvariantFlexibleProjection(
        listOf(
            genericComponentType.toKTypeProjection()
        )
    )
    else -> throw IllegalArgumentException("Unsupported type: $this")
}

private fun Type.toKType(): KType = toKTypeProjection().type!!