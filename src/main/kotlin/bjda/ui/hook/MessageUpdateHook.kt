package bjda.ui.hook

import bjda.plugins.ui.UIEvent
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.utils.messages.MessageEditData

open class MessageUpdateHook(private val message: Message) : UpdateHook() {
    override fun isIgnored(data: HookData): Boolean {
        return data.ignore.any {it.message == message.id}
    }

    override fun onUpdate(message: MessageEditData, data: HookData): RestAction<*> {
        return this.message.editMessage(message)
    }

    override fun listen() {
        UIEvent.listen(message.id, this)
    }

    override fun onDestroy() {
        this.message.delete().queue()
    }
}