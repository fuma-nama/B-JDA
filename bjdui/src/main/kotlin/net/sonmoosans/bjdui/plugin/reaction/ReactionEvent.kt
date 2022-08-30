package net.sonmoosans.bjdui.plugin.reaction

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class ReactionEvent : ListenerAdapter() {
    companion object {
        val listeners = HashMap<String, ReactionListener>()

        fun listen(messageId: String, listener: ReactionListener) {
            listeners[messageId] = listener
        }
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        listeners[event.messageId]?.onAdd(event)
    }

    override fun onMessageReactionRemove(event: MessageReactionRemoveEvent) {
        listeners[event.messageId]?.onRemove(event)
    }
}