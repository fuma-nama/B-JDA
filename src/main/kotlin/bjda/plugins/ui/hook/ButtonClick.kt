package bjda.plugins.ui.hook

import bjda.plugins.ui.UIEvent
import bjda.plugins.ui.event.ButtonListener
import bjda.ui.component.action.Button
import bjda.ui.types.Init
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class ButtonClick(
    id: String = UIEvent.createId(),
    private val handler: (ButtonInteractionEvent) -> Unit
) : EventHook(id), ButtonListener {
    init {
        UIEvent.listen(id, this)
    }

    override fun onClick(event: ButtonInteractionEvent) {
        handler(event)
    }

    override fun destroy() {

    }

    fun button(props: Init<Button.Props>): Button {
        return Button(id, props)
    }
}