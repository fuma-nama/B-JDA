package bjda.plugins.reaction.hooks

import bjda.ui.listener.ParsedHookData
import bjda.ui.listener.UIHook
import net.dv8tion.jda.api.entities.Message

fun interface ReactionReceiverHook : UIHook {
    fun receive(message: Message)

    override fun onUpdate(message: Message, data: ParsedHookData) = Unit

    override fun onDestroy() = Unit
}