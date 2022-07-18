package bjda.ui.hook

import bjda.ui.core.UI
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.RestAction

abstract class UpdateHook : UIHook {
    lateinit var unmount: () -> Unit
    abstract fun isIgnored(data: HookData): Boolean
    abstract fun onUpdate(message: Message, data: HookData): RestAction<*>
    abstract fun listen()

    final override fun onEnable(ui: UI) {
        unmount = {
            ui.hooks.remove(this)
        }

        listen()
    }
}