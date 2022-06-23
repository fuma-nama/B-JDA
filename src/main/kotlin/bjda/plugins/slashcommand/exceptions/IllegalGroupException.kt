package bjda.plugins.slashcommand.exceptions

import kotlin.reflect.KClass

class IllegalGroupException(message: String, from: KClass<*>) : RuntimeException("${message} at ${from}") {
}