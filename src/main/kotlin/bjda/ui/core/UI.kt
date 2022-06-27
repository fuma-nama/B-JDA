package bjda.ui.core

import bjda.ui.listener.InteractionUpdateHook
import bjda.ui.listener.UpdateHook
import bjda.ui.types.AnyComponent
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.Modal
import java.util.*

class UI<T: AnyComponent>(val root: T) {
    private val renderer = DefaultRenderer()
    private val hooks = HashMap<String, UpdateHook>()

    init {
        this.root.mount(null, this)
        renderer.renderComponent(this.root)
    }

    fun edit(callback: IMessageEditCallback) {
        callback.editMessage(this.build()).queue()
    }

    fun reply(message: Message) {
        message.reply(this.build()).queue()
    }

    fun reply(callback: IReplyCallback, ephemeral: Boolean = false) {
        callback.reply(this.build())
            .setEphemeral(ephemeral)
            .queue()
    }

    fun build(): Message {
        val data = RenderData()
        root.build(data)

        return data.build()
    }

    /**
     * Build a Modal
     *
     * Some components will be ignored as message type is unsupported by it
     *
     * Note: the title is assigned to the content of built message if not specified
     */
    fun buildModal(id: String, title: String? = null): Modal {
        val message = build()

        return Modal.create(id, title?: message.contentRaw)
            .addActionRows(message.actionRows)
            .build()
    }

    fun updateMessage() {
        val message = build()

        for (listener in hooks.values) {
            listener.onUpdate(message)
        }
    }

    fun updateComponent(element: AnyComponent) {
        renderer.addUpdateTask {
            element
        }
    }

    fun<S: Any> updateComponent(element: Component<*, S>, state: S) {
        renderer.addUpdateTask {
            element.update(state)

            element
        }
    }

    fun listen(hook: InteractionHook) {
        listen(InteractionUpdateHook(hook))
    }

    fun listen(entity: UpdateHook) {
        hooks[entity.id] = entity
    }

    fun destroy() {
        root.unmount()
        hooks.values.forEach { it.onDestroy() }
    }

    inner class DefaultRenderer : Renderer() {
        override fun onUpdated() {
            updateMessage()
        }

        override fun createScanner(element: AnyComponent): ComponentTreeScanner {
            return ComponentTreeScannerImpl(element)
        }
    }

    inner class ComponentTreeScannerImpl(val parent: AnyComponent) : ComponentTreeScanner() {
        override fun unmounted(comp: AnyComponent) {
            comp.unmount()
        }

        override fun mounted(comp: AnyComponent) {
            comp.mount(parent, this@UI)
        }

        override fun<P : IProps> reused(comp: Component<out P, *>, props: P) {
            comp.receiveProps(props)
        }
    }
}