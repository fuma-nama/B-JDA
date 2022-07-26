package bjda.ui.core

import bjda.ui.types.*

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