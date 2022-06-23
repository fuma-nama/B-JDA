package bjda.plugins.ui.hook

import bjda.plugins.ui.AutoReply
import bjda.plugins.ui.UIEvent
import bjda.plugins.ui.event.SelectListener
import bjda.ui.types.AnyComponent
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction

class MenuSelect(
    id: String = UIEvent.createId(),
    private val handler: (SelectMenuInteraction) -> AutoReply
) : EventHook(id), SelectListener {

    override fun onCreate(component: AnyComponent): String {
        UIEvent.listen(id, this)
        return super.onCreate(component)
    }

    override fun onSelect(event: SelectMenuInteraction) {
        handler(event).reply(ui, event)
    }
}