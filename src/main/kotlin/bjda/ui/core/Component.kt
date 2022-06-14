package bjda.ui.core

import bjda.ui.ComponentManager
import bjda.ui.exceptions.MissingManagerException

typealias FComponent = Component<*, *>
typealias Children = Array<FComponent>

abstract class Component<P, S : Any?>(val props: P, initialState: S, val key: Any? = null) {
    private var manager: ComponentManager? = null
    private var asset: AssetLevel? = null
    private var children: Array<FComponent>? = null
    var state: S = initialState
        set(value) {
            this.asset?.state = value
            field = value
        }

    protected abstract fun render(): Children?

    protected open fun onBuild(data: RenderData) {
    }

    open fun shouldUpdate(): Boolean {
        return true
    }

    fun build(data: RenderData) {
        if (this.manager == null)
            throw MissingManagerException()

        onBuild(data)

        var child = children

        if (shouldUpdate()) {
            child?.forEach { it.onUnmount() }

            child = render()

            if (child != null) {
                this.manager!!.mountChildren(this.asset!!, child)
            }
        }

        if (child != null) {
            for (component in child) {
                component.build(data)
            }
        }

        children = child
    }

    open fun mount(manager: ComponentManager, asset: AssetLevel) {
        this.manager = manager
        this.asset = asset

        asset.state?.let {
            this.state = it as S
        }
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
            return children.filterNotNull().toTypedArray()
        }
    }
}

