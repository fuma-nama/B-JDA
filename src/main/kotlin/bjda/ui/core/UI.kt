package bjda.ui.core

import bjda.ui.hook.*
import bjda.ui.types.AnyElement
import bjda.ui.types.AnyProps
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
        var afterUpdate: (ui: UI) -> Unit = {},
        /**
         * Fired when ui is destroying
         */
        var onDestroy: (ui: UI) -> Unit = {},
        /**
         * Renderer to render elements
         */
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
    private val updater = Updater()

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

    fun edit(message: Message, success: Consumer<Message>? = null) {
        updater.addTask(
            message.editMessage(this.build()),
            success
        )
    }

    fun edit(callback: IMessageEditCallback, success: Consumer<InteractionHook>? = null) {
        updater.addTask(
            callback.editMessage(this.build()),
            success
        )
    }

    fun edit(hook: InteractionHook, success: Consumer<Message>? = null) {
        updater.addTask(
            hook.editOriginal(this.build()),
            success
        )
    }

    /**
     * Edit event message then Update hooks which are not the same interaction
     */
    fun<T> editAndUpdate(event: T, success: Consumer<InteractionHook>? = null) where T: IMessageEditCallback {
        edit(event, success)

        updateHooks(event.hook)
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

    fun updateHooks(hook: InteractionHook) {
        if (hooks.isNotEmpty()) {

            hook.retrieveOriginal().queue {
                updateHooks(ignore(it))
            }
        }
    }

    fun updateHooks(ignore: List<Ignore>) {
        return updateHooks(HookData(ignore))
    }

    fun updateHooks(data: HookData = HookData()) {
        val message = build()

        for (hook in hooks) {
            if (hook !is UpdateHook || hook.isIgnored(data)) continue

            hook.onUpdate(message, data)?.queue()
        }
    }

    fun updateComponent(element: AnyElement) {
        renderer.addUpdateTask {
            Payload(element)
        }
    }

    fun updateComponent(
        element: AnyElement,
        event: IMessageEditCallback? = null,
        update: () -> Unit,
        onUpdated: (() -> Unit)? = null) {
        renderer.addUpdateTask {
            update()

            Payload(
                comp = element,
                afterUpdate = {
                    if (event != null)
                        editAndUpdate(event)

                    onUpdated?.invoke()
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

    /**
     * Destroy root element and ui hooks
     *
     * @see Option.onDestroy
     */
    fun destroy() {
        option.onDestroy(this)

        root?.unmount()
        root = null

        val itr = hooks.iterator()

        while (itr.hasNext()) {
            val hook = itr.next()
            itr.remove()

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
            option.afterUpdate(this@UI)
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

        override fun <P : AnyProps> reused(comp: Element<out P>, props: P) {
            comp.receiveProps(props)
        }
    }

    /**
     *
     * task task2 task3
     */
    class Updater {
        private var current: RestAction<*>? = null
        private var next: RestAction<*>? = null

        fun addTask(action: RestAction<*>) {
            if (current == null) {
                current = action

                update()
            } else {
                next = action
            }
        }

        fun<T> addTask(action: RestAction<T>, success: Consumer<T>?) {
            addTask(
                if (success == null) action else action.map(success::accept)
            )
        }

        private fun update() {
            current?.queue {
                current = next

                update()
            }
        }
    }
}