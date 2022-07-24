package bjda.plugins.reaction.hooks

import bjda.ui.core.UI
import bjda.ui.hook.UIHook
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.InteractionHook

fun interface ReactionReceiverHook : UIHook {
    fun receive(message: Message)

    override fun onDestroy() = Unit
}

/**
 * Enable the ReactionModule in the ui
 */
fun<H: UI> H.enableReaction(interaction: InteractionHook): H {
    interaction.retrieveOriginal().queue {
        enableReaction(it)
    }

    return this
}

/**
 * Enable the ReactionModule in the ui
 */
fun<H: UI> H.enableReaction(message: Message): H {
    for (hook in hooks) {
        if (hook is ReactionReceiverHook) {
            hook.receive(message)
        }
    }

    return this
}