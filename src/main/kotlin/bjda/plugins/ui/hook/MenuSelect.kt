package bjda.plugins.ui.hook

import bjda.plugins.ui.UIEvent
import bjda.plugins.ui.event.SelectListener
import bjda.ui.component.action.Menu
import bjda.ui.types.Init
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction

class MenuSelect(
    id: String = UIEvent.createId(),
    private val handler: (SelectMenuInteraction) -> Unit
) : EventHook(id), SelectListener {
    init {
        UIEvent.listen(id, this)
    }

    override fun onSelect(event: SelectMenuInteraction) {
        handler(event)
    }

    override fun destroy() {

    }

    fun menu(props: Init<Menu.Props>): Menu {
        return Menu(id, props)
    }
}