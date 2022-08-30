package net.sonmoosans.bjda.plugins.supercommand

import net.sonmoosans.bjda.plugins.supercommand.entries.CommandLocalization
import net.sonmoosans.bjda.plugins.supercommand.entries.PermissionEntry
import net.sonmoosans.bjda.plugins.supercommand.entries.SuperNode
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command.Type
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.internal.interactions.CommandDataImpl

abstract class SuperUserContext(
    name: String,
    guildOnly: Boolean? = null,
    permissions: DefaultMemberPermissions? = null
): SuperContext<UserContextInteractionEvent>(
    name, Type.USER, guildOnly, permissions
)

abstract class SuperMessageContext(
    name: String,
    guildOnly: Boolean? = null,
    permissions: DefaultMemberPermissions? = null
): SuperContext<MessageContextInteractionEvent>(
    name, Type.MESSAGE, guildOnly, permissions
)

abstract class SuperContext<E: GenericContextInteractionEvent<*>>(
    override val name: String,
    val type: Type,
    override val guildOnly: Boolean? = null,
    override val permissions: DefaultMemberPermissions? = null
) : SuperNode, PermissionEntry, CommandLocalization {
    open fun run(event: E) = Unit

    override fun build(listeners: Listeners): CommandData {
        val data = CommandDataImpl(type, name)
            .setLocalize()
            .setPermissions()

        listeners[ContextInfo(name, type)] = this

        return data
    }
}