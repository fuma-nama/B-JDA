package bjda.ui.core

import bjda.ui.core.hooks.Context
import bjda.ui.core.hooks.Delegate
import bjda.ui.core.internal.RenderData
import bjda.ui.types.*
import bjda.ui.utils.ComponentBuilder

fun parseChildren(children: Children): ComponentTree {
    return ComponentBuilder().apply(children).build().toTypedArray()
}

operator fun<T: Element<P>, P : CProps<C>, C: Any> T.rangeTo(v: P.() -> C): T {
    props.init(v)
    return this
}

operator fun<T: Element<P>, P : AnyProps> T.invoke(v: P.() -> Unit): T {
    props.apply(v)

    return this
}

operator fun<T: Element<P>, P: CProps<C>, C : Any> T.minus(v: C): T {
    props.children = v
    return this
}

interface Element<P : AnyProps> {
    var props: P
    var snapshot: ComponentTree?
    val contexts : ContextMap?

    fun mount(parent: AnyElement?, manager: UI?)

    fun receiveProps(next: Any?)

    /**
     * Build current component
     */
    fun build(data: RenderData) = Unit

    fun render(): ComponentTree? = null

    fun unmount()

    fun buildAll(data: RenderData) {
        build(data)
        buildChildren(data)
    }

    fun buildChildren(data: RenderData) {
        snapshot?.forEach { component ->
            component?.buildAll(data)
        }
    }

    fun<V> Context<V>.consumer(default: V): V {
        return contexts?.getOrDefault(this, default) as V
    }

    fun<V> Context<V>.consumerBy(default: V) = Delegate {
        contexts?.getOrDefault(this, default) as V
    }
}

abstract class ElementImpl<P : AnyProps>(override var props: P) : Element<P> {
    override val contexts: ContextMap?
        get() {
            return parent?.contexts
        }

    override var snapshot: ComponentTree? = null
    lateinit var ui: UI
    var parent: AnyElement? = null

    override fun mount(parent: AnyElement?, ui: UI?) {
        this.parent = parent

        if (ui != null) {
            this.ui = ui
        }
    }

    override fun receiveProps(next: Any?) {
        this.props = next as P
    }

    override fun unmount() {
        snapshot?.forEach {child ->
            child?.unmount()
        }
    }
}

typealias FElementBody<P> = FElement<P>.() -> Children?

fun interface FElementConstructor<P : AnyProps, C> {

    operator fun rangeTo(props: P.() -> C): FElement<P>
}

class FElement<P: AnyProps>(props: P, val body: FElementBody<P>) : ElementImpl<P>(props) {
    private var render: Children? = null
    var build: ((RenderData) -> Unit)? = null

    override fun mount(parent: AnyElement?, ui: UI?) {
        super.mount(parent, ui)

        this.render = body.invoke(this)
    }

    override fun render(): ComponentTree? {

        return render?.let {
            parseChildren(it)
        }
    }

    override fun build(data: RenderData) {
        this.build?.invoke(data)
    }

    companion object {
        fun<P: CProps<C>, C: Any> element(props: () -> P, body: FElementBody<P>): FElementConstructor<P, C> {
            return FElementConstructor { init ->

                FElement(props().init(init), body)
            }
        }

        fun element(body: FElementBody<IProps>): FElementConstructor<IProps, Unit> {
            return FElementConstructor { init ->

                FElement(IProps().init(init), body)
            }
        }
    }
}