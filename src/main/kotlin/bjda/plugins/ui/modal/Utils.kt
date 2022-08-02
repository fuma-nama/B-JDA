package bjda.plugins.ui.modal

import bjda.ui.component.action.Action
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

operator fun ModalInteractionEvent.get(id: String): String {
    return value(id)
}

fun ModalInteractionEvent.getOrNull(id: String): String? {
    return getValue(id)?.asString
}

fun ModalInteractionEvent.value(id: String): String {
    return getOrNull(id)!!
}

fun ModalInteractionEvent.value(item: Action): String {
    return value(item.id!!)
}