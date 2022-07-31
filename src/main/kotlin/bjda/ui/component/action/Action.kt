package bjda.ui.component.action

import net.dv8tion.jda.api.interactions.components.ActionComponent

interface Action {
    val id: String?
    fun build(): ActionComponent
}

fun ActionComponent.toAction(): Action {
    return object : Action {
        override val id: String? = this@toAction.id

        override fun build(): ActionComponent {
            return this@toAction
        }
    }
}