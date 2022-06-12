package bjda.utils

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

class Compare<R>(val obj: KClass<*>) {
    var result: R? = null

    constructor(obj: KType) : this(obj.classifier as KClass<*>)
    inline fun <reified C> case(then: () -> R): Compare<R> {
        if (result == null && obj.isSubclassOf(C::class)) {
            result = then()
        }
        return this
    }

    fun default(then: () -> R): R {
        return result ?: then()
    }
}