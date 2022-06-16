package bjda.ui.exceptions

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmName

class UnexpectedTypeException(name: String) : RuntimeException("Unexpected type: $name") {
    constructor(type: KClass<*>) : this(type.jvmName)
    constructor(type: KType) : this(type.classifier as KClass<*>)
}