package bjda.ui.core.hooks

import bjda.ui.types.AnyComponent

interface IHook<T> {
    fun onCreate(component: AnyComponent): T
}