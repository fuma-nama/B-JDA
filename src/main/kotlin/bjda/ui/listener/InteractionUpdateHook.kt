package bjda.ui.listener

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent
import net.dv8tion.jda.api.interactions.Interaction
import net.dv8tion.jda.api.interactions.InteractionHook

class InteractionUpdateHook(private val hook: InteractionHook): UIHook {
    override fun onUpdate(message: Message, data: ParsedHookData) {
        val ignore = data.get<Ignore>()

        if (ignore != null && ignore.isIgnored(hook.interaction))
            return

        hook.editOriginal(message).queue()
        println("update interaction: ${hook.interaction.id}")
    }

    override fun onDestroy() {
        hook.deleteOriginal().queue()
    }

    data class Ignore(val interactionId: String) : HookData {
        fun isIgnored(interaction: Interaction): Boolean {
            return interaction.id == this.interactionId
        }
    }
}