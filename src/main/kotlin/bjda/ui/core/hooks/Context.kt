package bjda.ui.core.hooks

import bjda.ui.types.AnyComponent

class Context<V: Any> private constructor() {
    inner class Consumer: IHook<V> {
        lateinit var component: AnyComponent

        override fun getValue(): V {
            return component.contexts[this@Context] as V
        }

        override fun onCreate(component: AnyComponent) {
            this.component = component
        }

        override fun onDestroy() = Unit
    }

    inner class Provider(private val value: V) : IHook<Unit> {
        override fun getValue() = Unit

        override fun onCreate(component: AnyComponent) {
            val contexts = HashMap(component.contexts)
            contexts[this@Context] = value

            component.contexts = contexts
        }

        override fun onDestroy() = Unit
    }

    companion object {
        fun<T: Any> create(): Context<T> {
            return Context()
        }
    }
}