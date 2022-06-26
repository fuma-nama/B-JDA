package bjda.plugins.ui.hook

import bjda.plugins.ui.UIEvent
import bjda.plugins.ui.hook.event.ButtonListener
import bjda.ui.component.action.Button
import bjda.ui.types.Init
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class ButtonClick(
    id: String = UIEvent.createId(),
    private val handler: (event: ButtonInteractionEvent) -> Unit
) : EventHook(id), ButtonListener {
    override fun onClick(event: ButtonInteractionEvent) {
        handler(event)
    }

    override fun listen(id: String) {
        UIEvent.listen(id, this)
    }

    override fun destroy(id: String) {
        UIEvent.buttons.remove(id)
    }
}