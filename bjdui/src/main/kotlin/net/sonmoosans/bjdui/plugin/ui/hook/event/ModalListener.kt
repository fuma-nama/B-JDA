package net.sonmoosans.bjda.plugins.ui.hook.event

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

fun interface ModalListener {
    fun onSubmit(event: ModalInteractionEvent)
}