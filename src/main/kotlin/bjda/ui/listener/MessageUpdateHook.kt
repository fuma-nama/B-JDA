package bjda.ui.listener

import net.dv8tion.jda.api.entities.Message

open class MessageUpdateHook(private val message: Message) : UIHook {
    override fun onUpdate(message: Message) {
        this.message.editMessage(message).queue()
    }

    override fun onDestroy() {
        this.message.delete().queue()
    }
}