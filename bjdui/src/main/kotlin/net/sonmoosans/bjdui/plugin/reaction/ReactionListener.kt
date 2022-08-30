package net.sonmoosans.bjdui.plugin.reaction

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent

interface ReactionListener {
    fun onAdd(event: MessageReactionAddEvent)
    fun onRemove(event: MessageReactionRemoveEvent)
}