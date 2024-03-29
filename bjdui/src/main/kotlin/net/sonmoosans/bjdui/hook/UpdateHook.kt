package net.sonmoosans.bjda.ui.hook

import net.sonmoosans.bjda.ui.core.UI
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.utils.messages.MessageEditData
import net.sonmoosans.bjdui.hook.HookData

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
    abstract fun onUpdate(message: MessageEditData, data: HookData): RestAction<*>?
    abstract fun listen()

    override fun onEnable(ui: UI) {
        this.ui = ui

        listen()
    }
}