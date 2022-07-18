package bjda.plugins.reaction.hooks

import bjda.ui.hook.UIHook
import net.dv8tion.jda.api.entities.Message

fun interface ReactionReceiverHook : UIHook {
    fun receive(message: Message)

    override fun onDestroy() = Unit
}