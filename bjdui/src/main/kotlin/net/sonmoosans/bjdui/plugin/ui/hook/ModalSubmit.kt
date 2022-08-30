package net.sonmoosans.bjda.plugins.ui.hook

import net.sonmoosans.bjda.plugins.ui.UIEvent
import net.sonmoosans.bjda.plugins.ui.hook.event.ModalListener
import net.sonmoosans.bjda.ui.core.hooks.Delegate
import net.sonmoosans.bjdui.types.AnyComponent
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

class ModalSubmit(
    id: String = UIEvent.createId(),
    private val handler: (ModalInteractionEvent) -> Unit
) : EventHook(id), ModalListener {

    override fun listen() {
        UIEvent.listen(id, this)
    }

    override fun onDestroy() {
        UIEvent.menus.remove(id)
    }

    override fun onSubmit(event: ModalInteractionEvent) {
        handler.invoke(event)
    }

    companion object {

        /**
         * Create and Use the hook and return its id as a delegate
         */
        fun AnyComponent.onSubmit(
            id: String = UIEvent.createId(),
            handler: (ModalInteractionEvent) -> Unit
        ): Delegate<String> {
            val hook = ModalSubmit(id, handler)

            return Delegate { this use hook }
        }

        /**
         * Listen modal submit events of specified id, but don't attach to any element
         *
         * @return menu id
         */
        fun onSubmitStatic(id: String = UIEvent.createId(), handler: (event: ModalInteractionEvent) -> Unit): String {
            ModalSubmit(id, handler).listen()
            return id
        }
    }
}