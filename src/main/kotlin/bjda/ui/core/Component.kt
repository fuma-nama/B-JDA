package bjda.ui.core

import bjda.utils.LambdaCreator

typealias FComponent = Component<*, *>
typealias Children =  Array<FComponent?>
typealias LambdaChildren = LambdaCreator<FComponent?>
typealias Key = Any

abstract class BasicComponent<P>(props: P, key: Key? = null) : Component<P, Unit>(props, key)

abstract class Component<P, S : Any>(var props: P, val key: Key? = null) {
    lateinit var manager: ComponentManager
    var children: Children? = null
    lateinit var state: S

    fun receiveProps(next: Any?) {
        val prev = this.props

        this.props = next as P
        onReceiveProps(prev, next)
    }

    /**
     * Render component children
     * should be called between update() and build()
     * Always invoked from its parent
     */
    open fun render(): LambdaChildren? {
        return null
    }

    /**
     * Build data to message
     */
    fun build(data: RenderData) {
        onBuild(data)

        children?.forEach {component ->
            component?.build(data)
        }
    }

    open fun onReceiveProps(prev: P, next: P) {
    }

    open fun onMount(manager: ComponentManager) {
    }

    open fun onBuild(data: RenderData) {
    }

    open fun onUnmount() {
    }

    fun forceUpdate() {
        manager.updateComponent(this)
    }

    fun updateState(updater: S.() -> Unit) {
        updater(this.state)
        updateState(this.state)
    }

    fun updateState(state: S) {
        manager.updateComponent(this, state)
    }
}
