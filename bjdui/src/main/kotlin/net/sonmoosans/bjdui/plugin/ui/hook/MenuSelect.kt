package net.sonmoosans.bjda.plugins.ui.hook

import net.sonmoosans.bjda.plugins.ui.UIEvent
import net.sonmoosans.bjda.plugins.ui.hook.event.SelectListener
import net.sonmoosans.bjda.ui.core.hooks.Delegate
import net.sonmoosans.bjdui.types.AnyComponent
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction

/**
 * Create a Select Event Listener and returns its id
 */
class MenuSelect(
    id: String = UIEvent.createId(),
    private val handler: (SelectMenuInteraction) -> Unit
) : EventHook(id), SelectListener {

    override fun listen() {
        UIEvent.listen(id, this)
    }

    override fun onSelect(event: SelectMenuInteraction) = handler(event)

    override fun onDestroy() {
        UIEvent.menus.remove(id)
    }

    companion object {
        /**
         * Create and Use the hook and return its id as a delegate
         */
        fun AnyComponent.onSelect(id: String = UIEvent.createId(), handler: (SelectMenuInteraction) -> Unit): Delegate<String> {
            val hook = MenuSelect(id, handler)

            return Delegate { this use hook }
        }

        /**
         * Listen menu events of specified id, but don't attach to any element
         *
         * @return menu id
         */
        fun onSelectStatic(id: String, handler: (event: SelectMenuInteraction) -> Unit): String {
            MenuSelect(id, handler).listen()
            return id
        }
    }
}