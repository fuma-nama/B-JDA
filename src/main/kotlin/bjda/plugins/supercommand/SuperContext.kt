package bjda.plugins.supercommand

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command.Type
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.internal.interactions.CommandDataImpl

abstract class SuperContext(override val name: String, val type: Type) : SuperNode {
    open fun run(event: MessageContextInteractionEvent) = Unit
    open fun run(event: UserContextInteractionEvent) = Unit

    fun build(): CommandData {
        return CommandDataImpl(type, name)
    }
}