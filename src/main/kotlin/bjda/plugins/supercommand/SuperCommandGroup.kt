package bjda.plugins.supercommand

import bjda.plugins.supercommand.entries.PermissionEntry
import bjda.plugins.supercommand.entries.SuperNode
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData
import net.dv8tion.jda.internal.interactions.CommandDataImpl

abstract class SuperCommandGroup(
    override val name: String,
    val description: String,
    override val guildOnly: Boolean? = null,
    override val permissions: DefaultMemberPermissions? = null,
): SuperNode, PermissionEntry {
    open fun groups(): Array<out SuperCommandGroup>? = null
    open fun commands(): Array<out SuperCommand>? = null

    override fun build(listeners: Listeners): CommandData {
        val data = CommandDataImpl(name, description).setPermissions()

        val commands = commands()
        val groups = groups()

        if (groups != null) {
            data.addSubcommandGroups(
                groups.map {g ->
                    g.buildSub(name, listeners)
                }
            )
        } else if (commands != null) {
            data.addSubcommands(
                commands.map {cmd ->
                    cmd.buildSub(name, listeners = listeners)
                }
            )
        }

        return data
    }

    private fun buildSub(group: String, listeners: Listeners): SubcommandGroupData {
        val data = SubcommandGroupData(name, description)
        val commands = commands()?: error("Sub command group cannot be empty or null")

        data.addSubcommands(commands.map {cmd ->
            cmd.buildSub(group, name, listeners)
        })

        return data
    }

    companion object {
        fun create(name: String, description: String, vararg commands: SuperCommand): SuperCommandGroup {

            return object : SuperCommandGroup(name, description) {
                override fun commands(): Array<out SuperCommand> {
                    return commands
                }
            }
        }

        fun create(name: String, description: String, vararg groups: SuperCommandGroup): SuperCommandGroup {

            return object : SuperCommandGroup(name, description) {
                override fun groups(): Array<out SuperCommandGroup> {
                    return groups
                }
            }
        }
    }
}