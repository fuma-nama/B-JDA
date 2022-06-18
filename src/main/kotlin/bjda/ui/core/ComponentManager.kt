package bjda.ui.core

import bjda.ui.listener.UpdateHook
import bjda.ui.types.FComponent
import net.dv8tion.jda.api.entities.Message
import java.util.*

class ComponentManager(private val root: FComponent) {
    private val renderer = DefaultRenderer()
    private val hooks = Stack<UpdateHook>()

    init {
        root.manager = this
        root.onMount(this)
        renderer.renderComponent(root)
    }

    fun build(): Message {
        val data = RenderData()
        root.build(data)

        return data.build()
    }

    fun updateMessage() {
        val message = build()

        for (listener in hooks) {
            listener.onUpdate(message)
        }
    }

    fun updateComponent(component: FComponent) {
        renderer.addUpdateTask {
            component
        }
    }

    fun<S: Any> updateComponent(component: Component<*, S>, state: S) {
        renderer.addUpdateTask {
            component.state = state

            component
        }
    }

    fun listen(entity: UpdateHook) {
        hooks.push(entity)
    }

    fun destroy() {
        hooks.forEach { it.onDestroy() }
    }

    inner class DefaultRenderer : Renderer(
        ComponentTreeScannerImpl()
    ) {
        override fun onUpdated() {
            updateMessage()
        }
    }

    inner class ComponentTreeScannerImpl : ComponentTreeScanner() {
        override fun unmounted(comp: FComponent) {
            comp.onUnmount()
        }

        override fun mounted(comp: FComponent) {
            val manager = this@ComponentManager
            comp.manager = manager

            comp.onMount(manager)
        }

        override fun<P : FProps> reused(comp: Component<P, *>, props: P) {
            comp.receiveProps(props)
        }
    }
}