package bjda.ui.listener

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.InteractionHook

class InteractionUpdateHook(private val hook: InteractionHook): UpdateHook {
    override val id = hook.interaction.id

    override fun onUpdate(message: Message) {
        hook.editOriginal(message).queue()
    }

    override fun onDestroy() {
        hook.deleteOriginal().queue()
    }
}