package bjda.plugins.command

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class CommandListener: ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        event.channel
    }
}