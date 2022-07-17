package bjda.ui.core

import bjda.ui.component.Fragment
import bjda.ui.core.hooks.IHook
import bjda.ui.listener.HookData
import bjda.ui.listener.InteractionUpdateHook
import bjda.ui.types.*
import bjda.utils.LambdaBuilder
import bjda.utils.build
import net.dv8tion.jda.api.interactions.Interaction
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

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

operator fun Collection<AnyComponent?>.not(): Fragment {
    return Fragment(this)
}

abstract class Component<P : IProps>(var props: P) {
    constructor(props: ()-> P): this(props())

    var snapshot: ComponentTree? = null
    var parent: AnyComponent? = null
    val hooks = ArrayList<IHook<*>>()
    lateinit var contexts : ContextMap
    lateinit var ui: UI

    /**
     * Render component children
     *
     * should be called between update() and build()
     *
     * Always invoked from its parent
     */
    open fun onRender(): Children = {}
    open fun onBuild(data: RenderData) = Unit
    open fun onReceiveProps(prev: P, next: P) = Unit
    open fun onMount() = Unit
    open fun onUnmount() = Unit

    fun forceUpdate() {
        ui.updateComponent(this)
    }

    fun<T> useState(initial: T): StateDelegate<T> {
        return StateDelegate(initial)
    }

    fun<T> useState(initial: () -> T): LinkedStateDelegate<T> {
        return LinkedStateDelegate(initial)
    }

    fun<T> useCombinedState(initial: T): StateWrapper<T> {
        return StateWrapper(initial)
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
        operator fun<T: Component<P>, P : IProps> T.rangeTo(v: Init<P>): T {
            props.init(v)

            return this
        }

        operator fun<T: Component<P>, P: CProps<C>, C : Any> T.minus(v: C): T {
            props.children = v
            return this
        }

        operator fun<T: Component<P>, P: CProps<C>, C : Any> T.div(v: P.() -> C): T {
            props.children = v(props)
            return this
        }
    }

    inner class LinkedStateDelegate<T>(val initial: () -> T): IStateDelegate<T>() {
        private var wrapper: Wrapper? = null

        override fun get(): T {
            val wrapper = wrapper?: return initial()

            return wrapper.value
        }

        override fun set(value: T) {
            this.wrapper = Wrapper(value)
        }

        inner class Wrapper(var value: T)
    }

    inner class StateDelegate<T>(var value: T): IStateDelegate<T>() {
        override fun set(value: T) {
            this.value = value
        }

        override fun get(): T {
            return value
        }
    }

    inner class StateWrapper<T>(var value: T) {
        fun get(): T {
            return value
        }

        fun ref(): KMutableProperty0<T> {
            return this::value
        }

        fun set(value: T) {
            ui.updateComponent(this@Component) {
                this.value = value
            }
        }

        fun set(event: IMessageEditCallback, value: T) {
            ui.updateComponent(this@Component, event) {
                this.value = value
            }
        }

        infix fun update(updater: T.() -> Unit) {
            ui.updateComponent(this@Component) {
                updater(value)
            }
        }

        fun update(event: IMessageEditCallback, updater: T.() -> Unit) {
            ui.updateComponent(this@Component, event) {
                updater(value)
            }
        }

        fun updater(): Updater<T> {
            return Updater(value, ::update, ::update)
        }
    }

    data class Updater<T>(
        val value: T,
        val updater: (T.() -> Unit) -> Unit,
        val eventUpdater: (event: IMessageEditCallback, T.() -> Unit) -> Unit
    )

    abstract inner class IStateDelegate<T> {
        abstract fun get(): T
        abstract fun set(value: T)

        operator fun getValue(thisRef: Nothing?, property: KProperty<*>): T {
            return get()
        }

        operator fun setValue(thisRef: Nothing?, property: KProperty<*>, value: T) {
            ui.updateComponent(this@Component) {
                set(value)
            }
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return get()
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            ui.updateComponent(this@Component) {
                set(value)
            }
        }
    }
}