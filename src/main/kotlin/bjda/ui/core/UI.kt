package bjda.ui.core

import bjda.ui.listener.*
import bjda.ui.types.AnyComponent
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.Modal
import java.util.function.Consumer
import kotlin.collections.ArrayList

open class UI(private val option: Option = Option()) {
    data class Option(
        /**
         * Fired after updating components
         */
        var afterUpdate: () -> Unit = {}
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
    val hooks = ArrayList<UIHook>()

    constructor(root: AnyComponent) : this() {
        this.root = root
    }

    constructor(root: AnyComponent, option: Option) : this(option) {
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

    /**
     * Edit event message then Update hooks which are not the same interaction
     */
    fun<T> editAndUpdate(event: T, success: Consumer<InteractionHook>? = null) where T: IMessageEditCallback {
        edit(event, success)

        event.hook.retrieveOriginal().queue {
            updateHooks( *ignore(it.interaction!!) )
        }
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

    fun updateHooks(vararg data: HookData) {
        val message = build()
        val parsedData = data.associateBy { it::class }

        for (hook in hooks) {
            hook.onUpdate(message, ParsedHookData(parsedData))
        }
    }

    fun updateComponent(element: AnyComponent = root!!, update: (() -> Unit)? = null) {
        renderer.addUpdateTask {
            update?.invoke()

            Payload(
                comp = element
            )
        }
    }

    fun updateComponent(element: AnyComponent = root!!, event: IMessageEditCallback, update: (() -> Unit)? = null) {
        renderer.addUpdateTask {
            update?.invoke()

            Payload(
                comp = element,
                afterUpdate = {
                    editAndUpdate(event)
                }
            )
        }
    }

    fun listen(hook: InteractionHook) {
        listen(InteractionUpdateHook(hook))
    }

    fun listen(message: Message) {
        listen(MessageUpdateHook(message))
    }

    fun listen(hook: UIHook) {
        hook.onEnable(this)
        hooks += hook
    }

    fun destroy() {
        root!!.unmount()

        for (hook in hooks) {
            hook.onDestroy()
        }
    }

    fun destroyHook(hook: UIHook) {
        hook.onDestroy()
        hooks.remove(hook)
    }

    inner class DefaultRenderer : Renderer() {
        override fun onUpdated() {
            option.afterUpdate()
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

        override fun<P : IProps> reused(comp: Component<out P>, props: P) {
            comp.receiveProps(props)
        }
    }
}