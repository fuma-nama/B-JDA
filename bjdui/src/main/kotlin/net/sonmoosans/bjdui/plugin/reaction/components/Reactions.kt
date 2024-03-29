package net.sonmoosans.bjda.plugins.reaction.components

import net.sonmoosans.bjdui.plugin.reaction.ReactionEvent
import net.sonmoosans.bjdui.plugin.reaction.ReactionListener
import net.sonmoosans.bjda.plugins.reaction.hooks.ReactionReceiverHook
import net.sonmoosans.bjda.plugins.ui.UIEvent
import net.sonmoosans.bjda.ui.core.CProps
import net.sonmoosans.bjda.ui.core.Component
import net.sonmoosans.bjda.ui.core.hooks.IHook
import net.sonmoosans.bjdui.types.AnyComponent
import net.sonmoosans.bjdui.types.Children
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent

class Reactions(val id: String = UIEvent.createId()) : Component<Reactions.Props>(Props()) {
    private val receiver = ReactionReceiver()

    class Props : CProps<Array<out Emoji>>() {
        var onAdd: ((MessageReactionAddEvent) -> Unit)? = null
        var onRemove: ((MessageReactionRemoveEvent) -> Unit)? = null

        /**
         * If enabled, event will be fired when reaction is added or removed by a bot
         */
        var allowBots: Boolean = false

        fun reactions(vararg reactions: Emoji): Array<out Emoji> {
            return reactions
        }
    }

    override fun onRender(): Children {
        this use receiver

        return {}
    }

    inner class ReactionReceiver : ReactionReceiverHook, IHook<Unit>, ReactionListener {
        override fun onAdd(event: MessageReactionAddEvent) {
            if (canPass(event)) {
                props.onAdd?.invoke(event)
            }
        }

        override fun onRemove(event: MessageReactionRemoveEvent) {
            if (canPass(event)) {
                props.onRemove?.invoke(event)
            }
        }

        override fun receive(message: Message) {
            ReactionEvent.listeners[message.id] = this

            for (reaction in props.children) {
                message.addReaction(reaction).queue()
            }
        }

        override fun onCreate(component: AnyComponent, initial: Boolean) {
            if (initial) {
                ui.listen(this)
            }
        }

        override fun onDestroy() {
            ui.destroyHook(this)
        }

        private fun canPass(event: GenericMessageReactionEvent): Boolean {
            val user = event.user?: return true

            if (user.isBot && !props.allowBots) {
                return false
            }

            if (user.id == event.jda.selfUser.id) {
                return false
            }

            return true
        }
    }
}