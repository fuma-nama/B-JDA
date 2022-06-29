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
import java.util.function.Consumer

open class UI(private val option: Option = Option()) {
    data class Option(
        /**
         * If enabled, Hooks will be updated after updating components
         */
        var updateHooks: Boolean = true
    )

    var root: AnyComponent? = null
        private set(value) {
            if (value != null) {
                value.mount(null, this)
                renderer.renderComponent(value)
            }

            field = value
        }

    internal val renderer = DefaultRenderer()
    val hooks = HashMap<String, UpdateHook>()

    constructor(root: AnyComponent) : this() {
        this.root = root
    }

    /**
     * Switch current root to another one
     * @param update If enabled, Hooks will be updated after changing the root
     */
    fun switchTo(root: AnyComponent, update: Boolean = true) {
        this.root = root

        if (update)
            updateHooks()
    }

    fun edit(callback: IMessageEditCallback, success: Consumer<InteractionHook>? = null) {
        callback.editMessage(this.build()).queue(success)
    }

    fun reply(message: Message, success: Consumer<Message>? = null) {
        message.reply(this.build()).queue(success)
    }

    fun reply(callback: IReplyCallback, ephemeral: Boolean = false, success: Consumer<InteractionHook>? = null) {
        callback.reply(this.build())
            .setEphemeral(ephemeral)
            .queue(success)
    }

    fun build(): Message {
        val data = RenderData()
        root!!.build(data)

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

    fun updateHooks() {
        val message = build()

        for (listener in hooks.values) {
            listener.onUpdate(message)
        }
    }

    fun updateComponent(element: AnyComponent = root!!) {
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
        root!!.unmount()
        hooks.values.forEach { it.onDestroy() }
    }

    inner class DefaultRenderer : Renderer() {
        override fun onUpdated() {
            if (option.updateHooks)
                updateHooks()
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