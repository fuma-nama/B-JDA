package bjda.ui.core

import bjda.ui.core.hooks.Context
import bjda.ui.types.*
import bjda.utils.build
import kotlin.reflect.KClass

open class IProps {
    var key: Key? = null
}

open class CProps<C : Any> : IProps() {
    lateinit var children: C

    operator fun C.not() {
        this@CProps.children = this@not
    }

    infix fun with(children: C) {
        this.children = children
    }
}

fun <T: IProps> T.init(init: Init<T>): T {
    init(this)

    return this
}

operator fun<T: Component<P, S>, P : IProps, S : Any> T.rangeTo(v: Init<P>): T {
    props.init(v)

    return this
}

operator fun<T: Component<P, S>, P: CProps<C>, S : Any, C : Any> T.plus(v: C): T {
    props.children = v
    return this
}

operator fun<T: Component<P, S>, P: CProps<C>, S : Any, C : Any> T.div(v: P.() -> C): T {
    props.children = v(props)
    return this
}

abstract class Component<P : IProps, S : Any>(var props: P) {
    lateinit var context: HashMap<Context<*>, Any?>
    lateinit var state: S

    abstract class NoState<P : IProps>(props: P) : Component<P, Unit>(props)

    /**
     * Render component children
     * should be called between update() and build()
     * Always invoked from its parent
     */
    open fun render(): Children? = null
    open fun build(data: RenderData) = Unit
    open fun onUpdateState(prev: S, next: S) = Unit
    open fun onReceiveProps(prev: P, next: P) = Unit
    open fun onMount() = Unit
    open fun onUnmount() = Unit

    fun attach(manager: ComponentManager) : Element {
        return Element(manager)
    }

    lateinit var forceUpdate: () -> Unit

    lateinit var setState: (S) -> Unit

    fun updateState(updater: S.() -> Unit) {
        updater(this.state)
        setState(this.state)
    }



    inner class Element(val manager: ComponentManager) {
        private val component = this@Component

        var props: P by component::props
        val key: Key? by props::key
        var elements: ElementTree? = null

        init {
            forceUpdate = {
                manager.updateComponent(this)
            }

            setState = { state ->
                manager.updateComponent(this, state)
            }
        }

        fun updateState(state: S) {
            val prev = component.state
            component.state = state

            onUpdateState(prev, state)
        }

        fun getComponentType(): KClass<out Component<P, S>> {
            return component::class
        }

        fun receiveProps(next: Any?) {
            onReceiveProps(props, next as P)

            this.props = next
        }

        fun mount() {
            onMount()
        }

        fun build(data: RenderData) {
            component.build(data)

            elements?.forEach { component ->
                component?.build(data)
            }
        }

        fun render(): ComponentTree? {
            return component.render()
                ?.build()
                ?.toTypedArray()
        }

        fun unmount() {
            onUnmount()
        }
    }
}