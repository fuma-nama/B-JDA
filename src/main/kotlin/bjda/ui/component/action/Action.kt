package bjda.ui.component.action

import net.dv8tion.jda.api.interactions.components.ItemComponent

interface Action {
    fun build(): ItemComponent
}