package bjda.ui.component.action

import net.dv8tion.jda.api.interactions.components.ItemComponent

interface Action {
    val id: String?
    fun build(): ItemComponent
}