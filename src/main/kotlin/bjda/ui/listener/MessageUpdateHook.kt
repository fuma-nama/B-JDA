package bjda.ui.listener

import net.dv8tion.jda.api.entities.Message

open class MessageUpdateHook(private val message: Message) : UIHook {
    override fun onUpdate(message: Message, data: ParsedHookData) {
        val ignore = data.get<Ignore>()

        if (ignore != null && ignore.messageId == message.id)
            return

        this.message.editMessage(message).queue()
    }

    override fun onDestroy() {
        this.message.delete().queue()
    }

    data class Ignore(val messageId: String): HookData
}