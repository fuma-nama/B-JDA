package bjda.ui.core

import bjda.ui.types.Elements
import bjda.ui.types.Key
import bjda.ui.types.Init
import bjda.ui.types.RenderContext

open class FProps {
    var key: Key? = null
}

fun <T: FProps> T.init(init: Init<T>): T {
    init(this)

    return this
}

abstract class Component<P : FProps, S : Any>(var props: P) {
    val key: Key? = props.key
    lateinit var manager: ComponentManager
    lateinit var state: S
    var context: RenderContext? = null

    constructor(props: P, init: Init<P>) : this(props.init(init))

    abstract class NoState<P : FProps> : Component<P, Unit> {
        constructor(props: P) : super(props)
        constructor(props: P, init: Init<P>) : super(props, init)
    }

    fun receiveProps(next: P) {
        onReceiveProps(this.props, next)

        this.props = next
    }

    /**
     * Render component children
     * should be called between update() and build()
     * Always invoked from its parent
     */
    open fun render(): Elements? {
        return null
    }

    /**
     * Build data to message
     */
    fun build(data: RenderData) {
        onBuild(data)

        context?.forEach { component ->
            component?.build(data)
        }
    }

    open fun onReceiveProps(prev: P, next: P) = Unit
    open fun onMount(manager: ComponentManager) = Unit
    open fun onBuild(data: RenderData) = Unit
    open fun onUnmount() = Unit

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
