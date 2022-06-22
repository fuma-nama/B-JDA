package bjda.plugins.ui

import bjda.ui.core.ComponentManager
import net.dv8tion.jda.api.interactions.components.ComponentInteraction

/**
 * Event ID is regenerated per instance,
 *
 * you should create instance outside the render method
 */
enum class AutoReply {
    OFF, REPEAT, EDIT;

    fun reply(manager: ComponentManager, event: ComponentInteraction) {
        when (this) {
            OFF -> return
            EDIT -> manager.edit(event)
            REPEAT -> manager.reply(event)
        }
    }
}