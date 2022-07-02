package bjda.plugins.supercommand

import bjda.plugins.supercommand.Listeners
import net.dv8tion.jda.api.interactions.commands.build.CommandData

sealed interface SuperNode {
    val name: String
    fun build(listeners: Listeners): CommandData
}