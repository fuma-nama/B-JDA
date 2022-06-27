package bjda.plugins.supercommand

import bjda.plugins.IModule
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData
import net.dv8tion.jda.internal.interactions.CommandDataImpl

class CommandGroupBuilder(val name: String) {
    val subgroups = ArrayList<SubcommandGroupData>()
    val subcommands = ArrayList<SubcommandData>()
}

class SuperCommandModule(vararg val commands: SuperCommand) : IModule {
    val listeners = HashMap<Info, SuperCommand>()

    data class Info(val group: String?, val subgroup: String?, val name: String)

    private fun getSubgroup(data: CommandGroupBuilder, name: String): SubcommandGroupData? {
        return data.subgroups.find {
            it.name == name
        }
    }

    private fun group(parent: CommandGroupBuilder?, cmd: SuperCommand): CommandGroupBuilder {
        cmd.group!!

        val group = parent?: CommandGroupBuilder(cmd.group)

        if (cmd.subgroup != null) {
            var subgroup = getSubgroup(group, cmd.subgroup) //cloned subgroup

            if (subgroup == null) {
                subgroup = SubcommandGroupData(cmd.subgroup, "No Description")

                group.subgroups.add(subgroup)
            }

            subgroup.addSubcommands(cmd.buildSub())
        } else {
            group.subcommands.add(cmd.buildSub())
        }

        return group
    }

    private fun parse(): HashMap<String, CommandData> {
        val built = HashMap<String, CommandData>()
        val groups = HashMap<String, CommandGroupBuilder>()

        for (cmd in commands) {

            if (cmd.group != null) {
                groups[cmd.group] = group(groups[cmd.group], cmd)
            } else {
                built[cmd.name] = cmd.build()
            }

            listeners[Info(cmd.group, cmd.subgroup, cmd.name)] = cmd
        }

        for (group in groups.values) {
            built[group.name] = CommandDataImpl(group.name, "Null")
                .addSubcommandGroups(group.subgroups)
                .addSubcommands(group.subcommands)
        }
        return built
    }

    override fun init(jda: JDA) {
        val commands = parse()

        jda.updateCommands().addCommands(commands.values).queue()

        jda.addEventListener(EventListener())
    }

    inner class EventListener : ListenerAdapter() {
        override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
            val info = if (event.subcommandName != null) {
                Info(event.name, event.subcommandGroup, event.subcommandName!!)
            } else {
                Info(null, null, event.name)
            }

            listeners[info]?.execute(event)
        }
    }
}