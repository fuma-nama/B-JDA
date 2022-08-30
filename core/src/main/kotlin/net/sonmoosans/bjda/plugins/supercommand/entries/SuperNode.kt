package net.sonmoosans.bjda.plugins.supercommand.entries

import net.sonmoosans.bjda.plugins.supercommand.Listeners
import net.dv8tion.jda.api.interactions.commands.build.CommandData

interface SuperNode {
    val name: String
    fun build(listeners: Listeners): CommandData
}