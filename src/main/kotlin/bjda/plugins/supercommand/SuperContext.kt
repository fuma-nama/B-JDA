package bjda.plugins.supercommand

import bjda.plugins.supercommand.entries.PermissionEntry
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command.Type
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.internal.interactions.CommandDataImpl

abstract class SuperContext(
    override val name: String,
    val type: Type,
    override val guildOnly: Boolean? = null,
    override val permissions: DefaultMemberPermissions? = null
) : SuperNode, PermissionEntry, NameLocalization {
    open fun run(event: MessageContextInteractionEvent) = Unit
    open fun run(event: UserContextInteractionEvent) = Unit

    override fun build(listeners: Listeners): CommandData {
        val data = CommandDataImpl(type, name)
            .setLocalName()
            .setPermissions()

        listeners[ContextInfo(name, type)] = this

        return data
    }
}