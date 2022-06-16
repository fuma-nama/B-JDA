package bjda.ui.listener

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.InteractionHook

class HookListener(val hook: InteractionHook): DataListener {
    override fun onUpdate(message: Message) {
        hook.editOriginal(message).queue()
    }

    override fun onDestroy() {
        hook.deleteOriginal().queue()
    }
}