package bjda.ui.hook

import bjda.plugins.ui.UIEvent
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.utils.messages.MessageEditData

open class InteractionUpdateHook(private val hook: InteractionHook): UpdateHook() {
    override fun isIgnored(data: HookData): Boolean {
        return data.ignore.any {it.interaction == hook.interaction.id}
    }

    override fun onUpdate(message: MessageEditData, data: HookData): RestAction<*> {
        return hook.editOriginal(message)
    }

    override fun listen() {
        hook.retrieveOriginal().queue {
            UIEvent.listen(it.id, this)
        }
    }

    override fun onDestroy() {
        hook.deleteOriginal().queue()
    }
}