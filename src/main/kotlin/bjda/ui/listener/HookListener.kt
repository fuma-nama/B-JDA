package bjda.ui.listener

import bjda.ui.ComponentManager
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.InteractionHook

class HookListener(val hook: InteractionHook): ComponentManager.DataListener {
    override fun onUpdate(message: Message) {
        hook.editOriginal(message).queue()
    }

    override fun onDestroy() {
        hook.deleteOriginal().queue()
    }
}