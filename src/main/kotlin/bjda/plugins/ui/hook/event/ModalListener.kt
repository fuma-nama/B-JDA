package bjda.plugins.ui.hook.event

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

interface ModalListener {
    fun onSubmit(event: ModalInteractionEvent)
}