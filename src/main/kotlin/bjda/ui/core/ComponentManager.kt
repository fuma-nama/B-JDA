package bjda.ui.core

import bjda.ui.listener.UpdateHook
import bjda.ui.types.AnyComponent
import bjda.ui.types.AnyElement
import net.dv8tion.jda.api.entities.Message
import java.util.*

class ComponentManager(root: AnyComponent) {
    private val root: AnyElement = root.attach(this)
    private val renderer = DefaultRenderer()
    private val hooks = Stack<UpdateHook>()

    init {
        this.root.mount(null)
        renderer.renderElement(this.root)
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

    fun updateComponent(element: AnyElement) {
        renderer.addUpdateTask {
            element
        }
    }

    fun<S: Any> updateComponent(element: Component<*, S>.Element, state: S) {
        renderer.addUpdateTask {
            element.updateState(state)

            element
        }
    }

    fun listen(entity: UpdateHook) {
        hooks.push(entity)
    }

    fun destroy() {
        hooks.forEach { it.onDestroy() }
    }

    inner class DefaultRenderer : Renderer() {
        override fun onUpdated() {
            updateMessage()
        }

        override fun createScanner(element: AnyElement): ComponentTreeScanner {
            return ComponentTreeScannerImpl(element)
        }
    }

    inner class ComponentTreeScannerImpl(parent: AnyElement) : ComponentTreeScanner(parent) {
        override fun unmounted(comp: AnyElement) {
            comp.unmount()
        }

        override fun mounted(comp: AnyComponent): AnyElement {
            val element = comp.attach(this@ComponentManager)
            element.mount(parent)

            return element
        }

        override fun<P : IProps> reused(comp: Component<out P, *>.Element, props: P) {
            comp.receiveProps(props)
        }
    }
}