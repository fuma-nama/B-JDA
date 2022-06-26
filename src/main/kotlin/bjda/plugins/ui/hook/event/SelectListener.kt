package bjda.plugins.ui.hook.event

import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction

interface SelectListener {
    fun onSelect(event: SelectMenuInteraction)
}