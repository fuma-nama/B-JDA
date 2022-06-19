package bjda.ui.core.hooks

import bjda.ui.types.AnyComponent

class Context<V: Any> private constructor(): IHook<V> {
    override fun onCreate(component: AnyComponent): V {
        return component.contexts[this] as V
    }

    inner class Provider(private val value: V) : IHook<Unit> {
        override fun onCreate(component: AnyComponent) {
            val contexts = HashMap(component.contexts)
            contexts[this@Context] = value

            component.contexts = contexts
        }
    }

    companion object {
        fun<T: Any> create(): Context<T> {
            return Context()
        }
    }
}