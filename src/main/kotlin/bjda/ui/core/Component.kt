package bjda.ui.core

import bjda.ui.core.hooks.IHook
import bjda.ui.types.*
import bjda.utils.build

open class IProps {
    var key: Key? = null
}

open class CProps<C : Any> : IProps() {
    open lateinit var children: C

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

abstract class Component<P : IProps, S : Any>(var props: P) {
    var snapshot: ComponentTree? = null
    var parent: AnyComponent? = null
    val hooks = ArrayList<IHook<*>>()
    open lateinit var state: S
    lateinit var contexts : ContextMap
    lateinit var ui: UI

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

    fun updateState(state: S) {
        ui.updateComponent(this, state)
    }

    fun updateState(updater: S.() -> Unit) {
        updater(this.state)
        this.updateState(state)
    }

    fun forceUpdate() {
        ui.updateComponent(this)
    }

    /**
     * Use a hook and return its value
     *
     * It should be used in render() function every time as it won't update its value after updating component
     *
     * Note: The Hook itself can be reused
     */
    infix fun<V> use(hook: IHook<V>): V {
        if (!hooks.contains(hook)) {
            hook.onCreate(this)
            hooks.add(hook)
        }

        return hook.getValue()
    }

    /**
     * Create a hook at global level
     *
     * You may use it anywhere, avoid to access its value outside the render function
     */
    fun<V> useLazy(hook: IHook<V>): Lazy<V> {
        return lazy {
            use(hook)
        }
    }

    internal fun update(state: S) {
        val prev = state
        this.state = state

        onUpdateState(prev, state)
    }

    internal fun mount(parent: AnyComponent?, manager: UI) {
        this.parent = parent
        this.ui = manager

        onMount()
    }

    internal fun receiveProps(next: Any?) {
        val prev = this.props
        this.props = next as P

        onReceiveProps(prev, next)
    }

    internal fun build(data: RenderData) {
        onBuild(data)

        val elements = this.snapshot?: throw IllegalStateException("Component should be rendered before build")

        elements.forEach { component ->
            component?.build(data)
        }
    }

    internal fun render(): ComponentTree {
        contexts = parent?.contexts ?: hashMapOf()

        return onRender()
            .build()
            .toTypedArray()
    }

    internal fun unmount() {
        onUnmount()

        snapshot?.forEach {child ->
            child?.unmount()
        }

        for (hook in hooks) {
            hook.onDestroy()
        }
    }

    companion object {
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
    }
}