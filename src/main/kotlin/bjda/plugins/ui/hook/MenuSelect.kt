package bjda.plugins.ui.hook

import bjda.plugins.ui.UIEvent
import bjda.plugins.ui.hook.event.SelectListener
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction

class MenuSelect(
    id: String = UIEvent.createId(),
    private val handler: (SelectMenuInteraction) -> Unit
) : EventHook(id), SelectListener {
    override fun listen(id: String) {
        UIEvent.listen(id, this)
    }

    override fun onSelect(event: SelectMenuInteraction) {
        handler(event)
    }

    override fun destroy(id: String) {
        UIEvent.menus.remove(id)
    }
}