package bjda.ui.core.hooks

import bjda.ui.types.AnyComponent

interface IHook<T> {
    fun getValue(): T
    fun onCreate(component: AnyComponent)
    fun onDestroy()
}