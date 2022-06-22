package bjda.ui.core

import bjda.ui.core.hooks.IHook
import bjda.ui.types.*
import bjda.utils.build

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

fun <T> T.init(init: Init<T>): T {
    init(this)

    return this
}

operator fun<T: Component<P, S>, P : IProps, S : Any> T.rangeTo(v: Init<P>): T {
    props.init(v)

    return this
}

operator fun<T: Component<P, S>, P: CProps<C>, S : Any, C : Any> T.minus(v: C): T {
    props.children = v
    return this
}

operator fun<T: Component<P, S>, P: CProps<C>, S : Any, C : Any> T.div(v: P.() -> C): T {
    props.children = v(props)
    return this
}

abstract class Component<P : IProps, S : Any>(var props: P) {
    var snapshot: ComponentTree? = null
    var parent: AnyComponent? = null
    val key: Key? by props::key
    lateinit var state: S
    lateinit var contexts : ContextMap
    lateinit var manager: ComponentManager

    abstract class NoState<P : IProps>(props: P) : Component<P, Unit>(props)
    /**
     * Render component children
     *
     * should be called between update() and build()
     *
     * Always invoked from its parent
     */
    open fun onRender(): Children = {}
    open fun onBuild(data: RenderData) = Unit
    open fun onUpdateState(prev: S, next: S) = Unit
    open fun onReceiveProps(prev: P, next: P) = Unit
    open fun onMount() = Unit
    open fun onUnmount() = Unit

    fun update(state: S) {
        val prev = state
        this.state = state

        onUpdateState(prev, state)
    }

    fun updateState(state: S) {
        manager.updateComponent(this, state)
    }

    fun updateState(updater: S.() -> Unit) {
        updater(this.state)
        this.updateState(state)
    }

    fun mount(parent: AnyComponent?, manager: ComponentManager) {
        this.parent = parent
        this.manager = manager

        onMount()
    }

    /**
     * Use a hook and return its value
     *
     * It should be used in render() function every time as it won't update its value after updating component
     *
     * The Hook can be reused
     */
    fun<V> use(hook: IHook<V>): V {
        return hook.onCreate(this)
    }

    fun forceUpdate() {
        manager.updateComponent(this)
    }

    fun receiveProps(next: Any?) {
        val prev = this.props
        this.props = next as P

        onReceiveProps(prev, next)
    }

    fun build(data: RenderData) {
        onBuild(data)

        val elements = this.snapshot?: throw IllegalStateException("Component should be rendered before build")

        elements.forEach { component ->
            component?.build(data)
        }
    }

    fun render(): ComponentTree {
        contexts = parent?.contexts ?: hashMapOf()

        return onRender()
            .build()
            .toTypedArray()
    }

    fun unmount() {
        onUnmount()
    }
}