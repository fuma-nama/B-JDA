package bjda.plugins.ui.hook

import bjda.plugins.ui.AutoReply
import bjda.plugins.ui.UIEvent
import bjda.plugins.ui.event.ButtonListener
import bjda.ui.core.ComponentManager
import bjda.ui.core.hooks.IHook
import bjda.ui.types.AnyComponent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class ButtonClick(
    private val id: String = UIEvent.createId(),
    private val handler: (ButtonInteractionEvent) -> AutoReply
) : IHook<String>, ButtonListener {
    lateinit var manager: ComponentManager

    override fun onCreate(component: AnyComponent): String {
        UIEvent.listen(id, this)
        this.manager = component.manager

        return id
    }

    override fun onClick(event: ButtonInteractionEvent) {

        handler(event).reply(manager, event)
    }
}