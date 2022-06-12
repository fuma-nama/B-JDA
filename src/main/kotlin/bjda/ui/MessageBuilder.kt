package bjda.ui

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.internal.entities.DataMessage

class MessageBuilder {
    fun build(jda: JDA): Message {
        jda.getGuildById(0)?.getTextChannelById(0).sendMessage()
        return DataMessage()
    }
}