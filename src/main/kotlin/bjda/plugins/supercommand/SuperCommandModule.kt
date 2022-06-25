package bjda.plugins.supercommand

import bjda.plugins.IModule
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData
import net.dv8tion.jda.internal.interactions.CommandDataImpl

class SuperCommandModule(vararg commands: SuperCommand) : IModule {
    val commands: HashMap<String, CommandDataImpl>
    val listeners = HashMap<Info, SuperCommand>()

    data class Info(val group: String?, val subgroup: String?, val name: String)

    init {
        val built = HashMap<String, CommandDataImpl>()

        fun getSubgroup(data: CommandDataImpl, name: String): SubcommandGroupData? {
            return data.subcommandGroups.find {it.name == name}
        }

        fun group(cmd: SuperCommand): CommandDataImpl {
            cmd.group!!

            val group = built[cmd.group]?: CommandDataImpl(cmd.group, "No Description")

            if (cmd.subgroup != null) {
                val subgroup = getSubgroup(group, cmd.subgroup)?: SubcommandGroupData(cmd.subgroup, "No Description").also {
                    group.addSubcommandGroups(it)
                }

                subgroup.addSubcommands(cmd.buildSub())
            } else {
                group.addSubcommands(cmd.buildSub())
            }

            return group
        }

        for (cmd in commands) {

            if (cmd.group != null) {
                built[cmd.group] = group(cmd)
            } else {
                built[cmd.name] = cmd.build()
            }

            listeners[Info(cmd.group, cmd.subgroup, cmd.name)] = cmd
        }

        this.commands = built
    }

    override fun init(jda: JDA) {
        for (data in commands.values) {
            jda.upsertCommand(data).queue()
        }

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