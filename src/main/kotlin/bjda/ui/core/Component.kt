package bjda.ui.core

import bjda.ui.exceptions.UnexpectedTypeException
import bjda.ui.ComponentManager
import bjda.ui.exceptions.MissingManagerException

typealias FComponent = Component<*, *>
typealias Children = Array<out FComponent?>
typealias Key = Any

/**
 * Base Component class,
 * it can also be a node of the component tree
 */
abstract class Component<P, S>(var props: P, initialState: S, val key: Key? = null) {
    private var manager: ComponentManager? = null
    var children: Children? = null
    var state: S = initialState

    fun receiveProps(props: Any?) {
        onReceiveProps(this.props, props as P)
    }

    /**
     * Render component children
     * should be called between update() and build()
     * Always invoked from its parent
     */
    abstract fun render(): Children?

    /**
     * Build data to message
     */
    fun build(data: RenderData) {
        onBuild(data)

        children?.forEach {component ->
            component?.build(data)
        }
    }

    protected open fun onReceiveProps(prev: P, props: P) {
        this.props = props
    }

    open fun onMount(manager: ComponentManager) {
        this.manager = manager
    }

    protected open fun onBuild(data: RenderData) {
    }

    open fun onUnmount() {
    }

    fun forceUpdate() {
        updateState(this.state)
    }

    fun updateState(state: S) {
        val manager = this.manager?: throw MissingManagerException()

        manager.updateComponent(this, state)
    }

    companion object {
        @Deprecated("Please use Component.children() instead", ReplaceWith("children"))
        fun child(vararg children: FComponent?): Children {
            return children
        }

        fun children(vararg children: Any?): Children {
            val components = ArrayList<FComponent?>()
            children.forEach {
                if (it == null) components.add(null)

                else when (it) {
                    is FComponent -> components += it
                    is Collection<*> -> components += it as Collection<FComponent?>
                    is Array<*> -> components += it as Array<FComponent?>
                    else -> throw UnexpectedTypeException(it::class)
                }
            }
            return components.toTypedArray()
        }
    }
}

