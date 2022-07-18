package bjda.plugins.ui

import bjda.plugins.ui.hook.event.ButtonListener
import bjda.plugins.ui.hook.event.ModalListener
import bjda.plugins.ui.hook.event.SelectListener
import bjda.ui.hook.UpdateHook
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
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

    override fun onMessageDelete(event: MessageDeleteEvent) {
        hooks.remove(event.messageId)?.forEach {
            it.unmount()
        }
    }

    companion object {
        val buttons = hashMapOf<String, ButtonListener>()
        val menus = hashMapOf<String, SelectListener>()
        val modals = hashMapOf<String, ModalListener>()
        val hooks = UpdateHookMap()

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

        fun listen(id: String, hook: UpdateHook) {
            hooks.listen(id, hook)
        }
    }
}

class UpdateHookMap : HashMap<String, ArrayList<UpdateHook>>() {
    fun listen(id: String, hook: UpdateHook) {
        if (containsKey(id)) {
            this[id]!! += hook
        } else {
            this[id] = arrayListOf(hook)
        }
    }
}