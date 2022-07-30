package bjda.ui.core

import bjda.ui.component.Fragment
import bjda.ui.core.hooks.Delegate
import bjda.ui.core.hooks.IHook
import bjda.ui.types.*
import bjda.utils.LambdaBuilder
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import kotlin.reflect.KProperty

open class IProps : CProps<Unit>()

open class CProps<C: Any> {
    var key: Key? = null
    open lateinit var children: C
}

fun <T> T.apply(apply: Apply<T>): T {
    apply(this)

    return this
}

fun <T: CProps<R>, R> T.init(init: T.() -> R): T {
    this.children = init(this)

    return this
}

class ComponentBuilder : LambdaBuilder<AnyElement?>() {

    /**
     * Add elements as a fragment
     */
    override operator fun Collection<AnyElement?>.unaryPlus() {
        elements += Fragment(this)
    }

    /**
     * Add elements as a fragment
     */
    override operator fun Array<out AnyElement?>.unaryPlus() {
        elements += Fragment(this)
    }
}

abstract class Component<P : CProps<*>>(props: P): ElementImpl<P>(props) {
    constructor(props: () -> P): this(props())

    val hooks = ArrayList<IHook<*>>()

    /**
     * Render component children
     *
     * should be called between update() and build()
     *
     * Always invoked from its parent
     */
    open fun onRender(): Children = {}
    override fun build(data: RenderData) = Unit
    open fun onReceiveProps(prev: P, next: P) = Unit
    open fun onMount() = Unit
    open fun onUnmount() = Unit

    fun forceUpdate() {
        ui.updateComponent(this)
    }

    fun<T> useState(initial: T): State<T> {
        return State(initial)
    }

    fun<T> useState(initial: T, onUpdate: () -> Unit): State<T> {
        return object : State<T>(initial) {
            override val onUpdated = onUpdate
        }
    }

    /**
     * Use a hook and return its value
     *
     * It should be used in render() function every time as it won't update its value after updating component
     *
     * Note: The Hook itself can be reused
     */
    infix fun<V> use(hook: IHook<V>): V {
        val initial = !hooks.contains(hook)
        if (initial) {
            hooks.add(hook)
        }

        return hook.onCreate(this, initial)
    }

    /**
     * Create a hook at global level
     *
     * You may use it anywhere, avoid to access its value outside the render function
     */
    fun<V> useBy(hook: IHook<V>): Delegate<V> {

        return Delegate {
            use(hook)
        }
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

    override fun mount(parent: AnyElement?) {
        super.mount(parent)

        onMount()
    }

    override fun mount(parent: AnyElement?, manager: UI) {
        super.mount(parent, manager)

        onMount()
    }

    override fun receiveProps(next: Any?) {
        val prev = this.props
        this.props = next as P

        onReceiveProps(prev, next)
    }

    override fun render(): ComponentTree {
        return parseChildren(onRender())
    }

    override fun unmount() {
        super.unmount()

        onUnmount()
        for (hook in hooks) {
            hook.onDestroy()
        }
    }

    open inner class State<T>(var value: T) {
        open val onUpdated: (() -> Unit)? = null

        fun get(): T {
            return value
        }

        fun<R> get(mapper: T.() -> R): R {
            return mapper(value)
        }

        private fun updateComponent(event: IMessageEditCallback? = null, update: () -> Unit) {
            ui.updateComponent(
                element = this@Component,
                event = event,
                update = update,
                onUpdated = onUpdated
            )
        }

        infix fun update(value: T) {
            updateComponent {
                this.value = value
            }
        }

        fun update(event: IMessageEditCallback, value: T) {
            updateComponent(event) {
                this.value = value
            }
        }

        infix fun update(updater: T.() -> Unit) {
            updateComponent {
                updater(value)
            }
        }

        fun update(event: IMessageEditCallback, updater: T.() -> Unit) {
            updateComponent(event) {
                updater(value)
            }
        }

        fun updater(): Updater<T> {
            return Updater(value, this::update, this::update)
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = get()

        operator fun getValue(thisRef: Nothing?, property: KProperty<*>): T = get()

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = update(value)

        operator fun setValue(thisRef: Nothing?, property: KProperty<*>, value: T) = update(value)
    }

    data class Updater<T>(
        val value: T,
        val updater: (T.() -> Unit) -> Unit,
        val eventUpdater: (event: IMessageEditCallback, T.() -> Unit) -> Unit
    )
}