package bjda.ui.core

import bjda.ui.hook.*
import bjda.ui.types.AnyElement
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.Modal
import net.dv8tion.jda.api.requests.RestAction
import java.util.function.Consumer
import kotlin.collections.ArrayList

open class UI(private val option: Option = Option()) {
    data class Option(
        /**
         * Fired after updating components
         */
        var afterUpdate: () -> Unit = {},
        var renderer: Renderer? = null
    )

    var root: AnyElement? = null
        private set(value) {
            if (value != null) {
                value.mount(null, this)
                renderer.renderElement(value)
            }

            field = value
        }

    private val renderer = option.renderer?: DefaultRenderer()

    val hooks = ArrayList<UIHook>()

    constructor(root: AnyElement) : this() {
        this.root = root
    }

    constructor(root: AnyElement, option: Option) : this(option) {
        this.root = root
    }

    /**
     * Switch current root to another one
     * @param update If enabled, Hooks will be updated after changing the root
     */
    fun switchTo(root: AnyElement, update: Boolean = true) {
        this.root = root

        if (update)
            updateHooks(HookData())
    }

    fun edit(callback: IMessageEditCallback, success: Consumer<InteractionHook>? = null) {
        callback.editMessage(this.build()).queue(success)
    }

    fun edit(hook: InteractionHook, success: Consumer<Message>? = null) {
        hook.editOriginal(this.build()).queue(success)
    }

    /**
     * Edit event message then Update hooks which are not the same interaction
     */
    fun<T> editAndUpdate(events: Array<T>, success: Consumer<InteractionHook>? = null) where T: IMessageEditCallback {
        for (event in events) {
            edit(event, success)
        }

        var queue: RestAction<*>? = null
        val originals = arrayListOf<Message>()

        events.forEach {
            val action = it.hook.retrieveOriginal().map {m -> originals += m }

            queue = if (queue != null) {
                queue!!.and(action)
            } else {
                action
            }
        }

        queue?.queue {
            updateHooks(ignore(originals))
        }
    }

    /**
     * Edit event message then Update hooks which are not the same interaction
     */
    fun<T> editAndUpdate(event: T, success: Consumer<InteractionHook>? = null) where T: IMessageEditCallback {
        edit(event, success)

        event.hook.retrieveOriginal().queue {
            updateHooks(ignore(it))
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

        root!!.buildAll(data)

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

    fun updateHooks(ignore: List<Ignore> = emptyList(), await: Boolean = false) {
        return updateHooks(HookData(ignore, await))
    }

    fun updateHooks(data: HookData = HookData()) {
        val message = build()

        for (hook in hooks) {
            if (hook !is UpdateHook) continue
            if (hook.isIgnored(data)) continue

            val action = hook.onUpdate(message, data)

            if (data.await) {
                action.complete()
            } else {
                action.queue()
            }
        }
    }

    fun updateComponent(element: AnyElement = root!!, event: IMessageEditCallback? = null, update: (() -> Unit)? = null) {
        renderer.addUpdateTask {
            update?.invoke()

            Payload(
                comp = element,
                afterUpdate = if (event != null) ({ editAndUpdate(event) }) else null
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
        root?.unmount()
        root = null

        for (hook in hooks) {
            hook.onDestroy()
        }
    }

    fun destroyHook(hook: UIHook) {
        hook.onDestroy()
        hooks.remove(hook)
    }

    inner class DefaultRenderer : Renderer() {
        private val scanner = ComponentTreeScannerImpl(this@UI)

        override fun onUpdated() {
            option.afterUpdate()
        }

        override fun getScanner(): ComponentTreeScanner {
            return scanner
        }
    }

    class ComponentTreeScannerImpl(val ui: UI) : ComponentTreeScanner() {
        override fun unmounted(comp: AnyElement) {
            comp.unmount()
        }

        override fun mounted(comp: AnyElement, parent: AnyElement) {
            comp.mount(parent, this.ui)
        }

        override fun<P : IProps> reused(comp: Element<out P>, props: P) {
            comp.receiveProps(props)
        }
    }
}