package bjda.ui.core

import bjda.ui.ComponentManager
import bjda.ui.exceptions.MissingManagerException

typealias FComponent = Component<*, *>
typealias Children = Array<out FComponent?>
typealias Key = Any

abstract class Component<P, S>(var props: P, initialState: S, val key: Key? = null) {
    private var manager: ComponentManager? = null
    private var children: Children? = null
    var state: S = initialState

    fun update(prop: Any?) {
        val prev = this.props
        this.props = prop as P

        onUpdate(prev, this.props)
    }

    protected abstract fun render(): Children?

    open fun shouldRenderChildren(): Boolean {
        return true
    }

    fun build(data: RenderData) {
        val manager = this.manager ?: throw MissingManagerException()

        onBuild(data)

        var child = children

        if (shouldRenderChildren()) {
            val snapshot = child

            child = manager.scanner.scan(snapshot, render())
        }

        if (child != null) {
            for (component in child) {
                component?.build(data)
            }
        }

        children = child
    }

    protected open fun onUpdate(prev: P, props: P) {
        this.props = props
    }

    open fun onMount(manager: ComponentManager) {
        this.manager = manager
    }

    protected open fun onBuild(data: RenderData) {
    }

    open fun onUnmount() {
    }

    fun updateState(state: S) {
        this.state = state

        if (manager == null) {
            throw MissingManagerException()
        }
        manager!!.update()
    }

    companion object {
        fun child(vararg children: FComponent?): Children {
            return children
        }

        fun child(children: List<FComponent?>): Children {
            return children.toTypedArray()
        }
    }
}

