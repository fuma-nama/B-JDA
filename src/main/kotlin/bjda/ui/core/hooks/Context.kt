package bjda.ui.core.hooks

import bjda.ui.core.CProps
import bjda.ui.utils.ComponentBuilder
import bjda.ui.core.ElementImpl
import bjda.ui.core.RenderData
import bjda.ui.types.AnyComponent
import bjda.ui.types.Children
import bjda.ui.types.ComponentTree
import bjda.ui.types.ContextMap

class Context<V> private constructor() {
    inner class Consumer(private val default: V): IHook<V> {
        override fun onCreate(component: AnyComponent, initial: Boolean): V {
            return component.contexts?.getOrDefault(this@Context, default) as V
        }

        override fun onDestroy() = Unit
    }

    data class Props<T>(val value: T) : CProps<Children>()
    inner class Provider(value: V) : ElementImpl<Props<V>>(Props(value)) {
        override val contexts: ContextMap =
            if (parent?.contexts == null) HashMap()
            else HashMap(parent?.contexts)

        override fun build(data: RenderData) = Unit

        override fun render(): ComponentTree {
            contexts[this@Context] = props.value

            return ComponentBuilder().apply(props.children)
                .build().toTypedArray()
        }
    }

    companion object {
        fun<T> create(): Context<T> {
            return Context()
        }
    }
}