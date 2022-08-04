package bjda.ui.core

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

    /**
     * When no UI provided
     */
    fun mount(parent: AnyElement?)

    fun mount(parent: AnyElement?, manager: UI)

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
}

abstract class ElementImpl<P : AnyProps>(override var props: P) : Element<P> {
    override val contexts: ContextMap?
        get() {
            return parent?.contexts
        }

    override var snapshot: ComponentTree? = null
    lateinit var ui: UI
    var parent: AnyElement? = null

    override fun mount(parent: AnyElement?) {
        this.parent = parent
    }

    override fun mount(parent: AnyElement?, manager: UI) {
        this.parent = parent
        this.ui = manager
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
typealias FElementBody<P> = FElement<P>.() -> Children
typealias FElementConstructor<P, C> = (props: P.() -> C) -> FElement<P>

class FElement<P: AnyProps>(props: P, val body: FElementBody<P>) : ElementImpl<P>(props) {
    private lateinit var render: Children
    var build: ((RenderData) -> Unit)? = null

    override fun mount(parent: AnyElement?) {
        super.mount(parent)
        this.render = body.invoke(this)
    }

    override fun render(): ComponentTree {
        return parseChildren(render)
    }

    override fun build(data: RenderData) {
        this.build?.invoke(data)
    }

    companion object {
        fun<P: CProps<C>, C: Any> element(props: () -> P, body: FElementBody<P>): FElementConstructor<P, C> {
            return { init ->
                FElement(props().init(init), body)
            }
        }

        fun element(body: FElementBody<IProps>): FElementConstructor<IProps, Unit> {
            return { init ->
                FElement(IProps().init(init), body)
            }
        }
    }
}