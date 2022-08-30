package net.sonmoosans.bjda.plugins.ui.hook

import net.sonmoosans.bjda.plugins.ui.UIEvent
import net.sonmoosans.bjda.plugins.ui.hook.event.ButtonListener
import net.sonmoosans.bjda.ui.core.hooks.Delegate
import net.sonmoosans.bjdui.types.AnyComponent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction

/**
 * Create a Click Event Listener and returns its id
 */
class ButtonClick(
    id: String = UIEvent.createId(),
    private val handler: (event: ButtonInteractionEvent) -> Unit
) : EventHook(id), ButtonListener {
    override fun onClick(event: ButtonInteractionEvent) {
        handler(event)
    }

    override fun listen() {
        UIEvent.listen(id, this)
    }

    override fun onDestroy() {
        UIEvent.buttons.remove(id)
    }

    companion object {
        /**
         * Create and Use the hook and return its id as a delegate
         */
        fun AnyComponent.onClick(id: String = UIEvent.createId(), handler: (event: ButtonInteractionEvent) -> Unit): Delegate<String> {
            val hook = ButtonClick(id, handler)

            return Delegate { this use hook }
        }

        /**
         * Listen button events of specified id, but don't attach to any element
         *
         * @return button id
         */
        fun onClickStatic(id: String, handler: (event: ButtonInteraction) -> Unit): String {
            ButtonClick(id, handler).listen()
            return id
        }
    }
}