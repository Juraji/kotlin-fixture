package com.marcellogalhardo.fixture.provider

import com.marcellogalhardo.fixture.FixtureRandom
import com.marcellogalhardo.fixture.NextFunction
import com.marcellogalhardo.fixture.external.getKType
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KVisibility
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.kotlinFunction

class ReflectTypeProvider(
    private val reflectNextOfFunction: NextFunction,
    private val fixtureRandom: FixtureRandom,
    private val config: ReflectTypeProviderConfig = ReflectTypeProviderConfig()
) {

    fun nextRandomInstance(classRef: KClass<*>, type: KType): Any? {
        // Nullable variables will always returns null.
        if (type.isMarkedNullable) return null

        // Check for a standard instance (e.g., Int or String).
        val primitive = nextStandardOrNull(classRef, type)
        if (primitive != null) {
            return primitive
        }

        // If is a Sealed Class, get the first sealed sub class
        if (classRef.isSealed) {
            val sealedSubClass = classRef.sealedSubclasses.firstOrNull()
            if (sealedSubClass != null) {
                return reflectNextOfFunction(sealedSubClass, type)
            }
        }

        // If it is an Interface, creates a Proxy instance.
        val paramClass = classRef.javaObjectType
        if (paramClass.isInterface) {
            return Proxy.newProxyInstance(
                paramClass.classLoader,
                arrayOf(paramClass)
            ) { _: Any, method: Method, _: Array<out Any> ->
                val methodReturnType = method.kotlinFunction
                    ?.returnType
                    ?.jvmErasure
                if (methodReturnType != null) {
                    reflectNextOfFunction(methodReturnType, type)
                } else {
                    null
                }
            }
        }

        // Get the first non-private constructor with the least number of arguments.
        val constructors = classRef.constructors
            .sortedBy { it.parameters.size }

        // If it doesn't have a constructor, try to check if it is an Object Type.
        if (constructors.isEmpty()) {
            return classRef.objectInstance ?: classRef.createInstance()
        } else {
            for (constructor in constructors) {
                // I need to filter here, otherwise it might think it is an object.
                if (constructor.visibility != KVisibility.PRIVATE) {
                    val arguments = constructor.parameters
                        .map { nextParameter(it.type, classRef, type) }
                        .toTypedArray()

                    return constructor.call(*arguments)
                }
            }
        }

        throw NoUsableConstructor()
    }

    private fun nextStandardOrNull(classRef: KClass<*>, type: KType): Any? = when (classRef) {
        Any::class -> fixtureRandom.nextAny()
        Boolean::class -> fixtureRandom.nextBoolean()
        Char::class -> fixtureRandom.nextChar()
        Double::class -> fixtureRandom.nextDouble()
        Float::class -> fixtureRandom.nextFloat()
        Int::class -> fixtureRandom.nextInt()
        Long::class -> fixtureRandom.nextLong()
        String::class -> fixtureRandom.nextString()
        List::class, Collection::class -> nextRandomList(classRef, type)
        Map::class -> nextRandomMap(classRef, type)
        else -> null
    }

    private fun nextParameter(
        paramType: KType,
        classRef: KClass<*>,
        classType: KType
    ): Any? {
        // Nullable variables will always returns null.
        if (paramType.isMarkedNullable) return null

        return when (val classifier = paramType.classifier) {
            is KClass<*> -> {
                reflectNextOfFunction(classifier, paramType)
            }
            is KTypeParameter -> {
                val typeParameterName = classifier.name
                val typeParameterId =
                    classRef.typeParameters.indexOfFirst { it.name == typeParameterName }
                val parameterType = classType.arguments[typeParameterId].type ?: getKType<Any>()
                reflectNextOfFunction(
                    parameterType.classifier as KClass<*>,
                    parameterType
                )
            }
            else -> {
                throw Error("Type of the classifier $classifier is not supported")
            }
        }
    }

    private fun nextRandomList(classRef: KClass<*>, type: KType): List<Any?> {
        val numOfElements = fixtureRandom.nextInt(config.collectionRange)

        val elemType = type.arguments[0].type!!

        return (1..numOfElements)
            .map { nextParameter(elemType, classRef, type) }
    }

    private fun nextRandomMap(classRef: KClass<*>, type: KType): Map<Any?, Any?> {
        val numOfElements = fixtureRandom.nextInt(config.collectionRange)

        val keyType = type.arguments[0].type!!
        val valType = type.arguments[1].type!!

        val keys = (1..numOfElements)
            .map { nextParameter(keyType, classRef, type) }
        val values = (1..numOfElements)
            .map { nextParameter(valType, classRef, type) }
        return keys.zip(values).toMap()
    }
}

inline fun <reified T : Any> ReflectTypeProvider.next(): T {
    val kType = getKType<T>()
    return nextRandomInstance(T::class, kType) as T
}

class ReflectTypeProviderConfig(
    var collectionRange: IntRange = 1..5
)

class NoUsableConstructor : Error()