package bjda.plugins.ui

import bjda.ui.core.UI
import net.dv8tion.jda.api.interactions.ModalInteraction
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.ComponentInteraction

/**
 * Event ID is regenerated per instance,
 *
 * you should create instance outside the render method
 */
enum class AutoReply {
    OFF, REPEAT, EDIT;

    fun<T> reply(ui: UI, event: T) where T: IReplyCallback, T: IMessageEditCallback {
        when (this) {
            OFF -> return
            EDIT -> ui.edit(event)
            REPEAT -> ui.reply(event)
        }
    }
}