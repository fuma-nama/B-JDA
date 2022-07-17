package bjda.plugins.reaction.hooks

import bjda.ui.core.UI
import bjda.ui.listener.InteractionUpdateHook
import bjda.ui.listener.MessageUpdateHook
import bjda.ui.listener.UIHook
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.requests.RestAction

class ReactionProviderHook(private val message: Message, hook: UIHook) : UIHook by hook {
    constructor(message: Message): this(message, MessageUpdateHook(message))
    constructor(message: Message, hook: InteractionHook): this(message, InteractionUpdateHook(hook))

    override fun onEnable(ui: UI) {
        for (hook in ui.hooks) {
            if (hook is ReactionReceiverHook) {
                hook.receive(message)
            }
        }
    }

    companion object {
        fun from(hook: InteractionHook): RestAction<ReactionProviderHook> {
            return hook.retrieveOriginal().map {
                ReactionProviderHook(it, hook)
            }
        }

        fun<H: UIHook> H.supportReaction(message: Message): ReactionProviderHook {
            return ReactionProviderHook(message)
        }
    }
}