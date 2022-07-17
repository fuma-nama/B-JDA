package bjda.ui.listener

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.Interaction
import net.dv8tion.jda.api.interactions.InteractionHook
import kotlin.reflect.KClass

interface HookData
typealias HookDataList = Array<out HookData>

/**
 * Build an array of HookData that will let UpdateHooks ignore given event
 *
 * Notice that Interaction from components event are different from root interaction
 *
 * Therefore, you must use hook.retrieveOriginal() to get the root interaction from message
 */
fun ignore(vararg event: Any): HookDataList {
    return event.map {
        when (it) {
            is InteractionHook -> InteractionUpdateHook.Ignore(it.interaction.id)
            is Interaction -> InteractionUpdateHook.Ignore(it.id)
            is Message.Interaction -> InteractionUpdateHook.Ignore(it.id)
            is Message -> MessageUpdateHook.Ignore(it.id)
            else -> throw IllegalArgumentException()
        }
    }.toTypedArray()
}

class ParsedHookData(val map: Map<KClass<out HookData>, HookData>) {
    inline fun<reified T: HookData> get(): T? {
        return map[T::class] as T?
    }
}

class Await : HookData