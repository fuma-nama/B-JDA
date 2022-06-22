package bjda.plugins.ui

import bjda.plugins.ui.event.ButtonListener
import bjda.plugins.ui.event.ModalListener
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.math.BigInteger

class UIEvent : ListenerAdapter() {
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val listener = buttons[event.componentId]

        listener?.onClick(event)
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {
        val listener = modals[event.modalId]

        listener?.onSubmit(event)
    }

    companion object {
        val buttons = hashMapOf<String, ButtonListener>()
        val modals = hashMapOf<String, ModalListener>()
        var id: BigInteger = BigInteger.ZERO

        fun createId(): String {
            return (id++).toString()
        }

        fun listen(id: String, listener: ButtonListener) {
            buttons[id] = listener
        }

        fun listen(id: String, listener: ModalListener) {
            modals[id] = listener
        }
    }
}