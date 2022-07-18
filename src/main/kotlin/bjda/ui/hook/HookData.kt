package bjda.ui.hook

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.Interaction
import net.dv8tion.jda.api.interactions.InteractionHook
import kotlin.reflect.KClass
data class Ignore(val interaction: String? = null, val message: String? = null)

class HookData(val ignore: List<Ignore> = emptyList(), val await: Boolean = false)

/**
 * Build an array of HookData that will let UpdateHooks ignore given event
 *
 * Notice that Interaction from components event are different from root interaction
 *
 * Therefore, you must use hook.retrieveOriginal() to get the root interaction from message
 *
 * @param event Recommend to input a message instance instead of interaction since related message update hooks will still be updated
 */
fun ignore(vararg event: Any): List<Ignore> {
    return event.map {
        when (it) {
            is InteractionHook -> Ignore(interaction = it.interaction.id)
            is Interaction -> Ignore(interaction = it.id)
            is Message.Interaction -> Ignore(interaction = it.id)
            is Message -> Ignore(message = it.id, interaction = it.interaction?.id)
            else -> throw IllegalArgumentException()
        }
    }
}