package net.sonmoosans.bjda.ui.core.hooks

import net.sonmoosans.bjda.ui.core.CProps
import net.sonmoosans.bjdui.utils.ComponentBuilder
import net.sonmoosans.bjda.ui.core.ElementImpl
import net.sonmoosans.bjda.ui.core.minus
import net.sonmoosans.bjdui.types.AnyComponent
import net.sonmoosans.bjdui.types.Children
import net.sonmoosans.bjdui.types.ComponentTree
import net.sonmoosans.bjdui.types.ContextMap

class Context<V> private constructor() {
    @Deprecated("Use consumer instead", replaceWith = ReplaceWith("consumer(default)"))
    inner class Consumer(private val default: V): IHook<V> {
        override fun onCreate(component: AnyComponent, initial: Boolean): V {
            return component.contexts?.getOrDefault(this@Context, default) as V
        }

        override fun onDestroy() = Unit
    }

    data class Props<T>(val value: T) : CProps<Children>()
    inner class Provider(value: V) : ElementImpl<Props<V>>(Props(value)) {
        constructor(value: V, children: Children) : this(value) {
            this-children
        }

        override val contexts: ContextMap =
            if (parent?.contexts == null) HashMap()
            else HashMap(parent?.contexts)

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