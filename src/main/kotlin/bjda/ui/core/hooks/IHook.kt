package bjda.ui.core.hooks

import bjda.ui.types.AnyComponent

interface IHook<T> {
    fun onCreate(component: AnyComponent, initial: Boolean): T
    fun onDestroy()
}

fun interface Delegate<T> {
    operator fun getValue(parent: Nothing?, property: Any?): T {
        return getValue()
    }

    operator fun getValue(parent: Any?, property: Any?): T {
        return getValue()
    }

    fun getValue(): T
}