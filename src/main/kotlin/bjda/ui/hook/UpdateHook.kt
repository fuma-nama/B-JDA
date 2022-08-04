package bjda.ui.hook

import bjda.ui.core.UI
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.RestAction

abstract class UpdateHook : UIHook {

    open lateinit var ui: UI

    /**
     * Update hooks will be unmounted after related messages are destroyed
     *
     * Notice that It is different from onDestroy
     */
    open fun unmount() {
        ui.hooks.remove(this)
    }

    abstract fun isIgnored(data: HookData): Boolean
    abstract fun onUpdate(message: Message, data: HookData): RestAction<*>?
    abstract fun listen()

    override fun onEnable(ui: UI) {
        this.ui = ui

        listen()
    }
}