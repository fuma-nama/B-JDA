package bjda.plugins.ui.hook.event

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

fun interface ButtonListener {
    fun onClick(event: ButtonInteractionEvent)
}