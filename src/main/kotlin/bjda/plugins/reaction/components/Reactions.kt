package bjda.plugins.reaction.components

import bjda.plugins.reaction.ReactionEvent
import bjda.plugins.reaction.ReactionListener
import bjda.plugins.reaction.hooks.ReactionReceiverHook
import bjda.plugins.ui.UIEvent
import bjda.ui.core.CProps
import bjda.ui.core.Component
import bjda.ui.core.hooks.IHook
import bjda.ui.types.AnyComponent
import bjda.ui.types.Children
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent

class Reactions(val id: String = UIEvent.createId()) : Component.NoState<Reactions.Props>(Props()) {
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

        override fun getValue() = Unit

        override fun onCreate(component: AnyComponent) {
            ui.listen(this)
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