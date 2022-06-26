package bjda.plugins.ui

import bjda.plugins.ui.hook.event.ButtonListener
import bjda.plugins.ui.hook.event.ModalListener
import bjda.plugins.ui.hook.event.SelectListener
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.UUID

class UIEvent : ListenerAdapter() {
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val listener = buttons[event.componentId]

        listener?.onClick(event)
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {
        val listener = modals[event.modalId]

        listener?.onSubmit(event)
    }

    override fun onSelectMenuInteraction(event: SelectMenuInteractionEvent) {
        val listener = menus[event.componentId]

         listener?.onSelect(event)
    }

    companion object {
        val buttons = hashMapOf<String, ButtonListener>()
        val menus = hashMapOf<String, SelectListener>()
        val modals = hashMapOf<String, ModalListener>()

        fun createId(): String {

            return UUID.randomUUID().toString()
        }

        fun listen(id: String, listener: ButtonListener) {
            buttons[id] = listener
        }

        fun listen(id: String, listener: ModalListener) {
            modals[id] = listener
        }

        fun listen(id: String, listener: SelectListener) {
            menus[id] = listener
        }
    }
}