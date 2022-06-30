package bjda.plugins.supercommand

import bjda.plugins.IModule
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData
import net.dv8tion.jda.internal.interactions.CommandDataImpl

class SuperCommandModule(vararg val nodes: SuperNode) : IModule {
    val listeners = HashMap<Info, SuperCommand>()
    val contexts = HashMap<ContextInfo, SuperContext>()

    data class Info(val group: String? = null, val subgroup: String? = null, val name: String)
    data class ContextInfo(val name: String, val type: Command.Type)

    private fun nextSubNode(group: String, node: SuperCommandGroup): SubcommandGroupData {
        val data = SubcommandGroupData(node.name, node.description)
        val commands = node.commands()?: error("Sub command group cannot be empty or null")

        data.addSubcommands(commands.map {cmd ->
            listeners[Info(group, node.name, cmd.name)] = cmd

            cmd.buildSub()
        })

        return data
    }

    private fun nextNode(node: SuperNode): CommandData {
        when (node) {
            is SuperCommand -> {
                return node.build().also {
                    listeners[Info(name = node.name)] = node
                }
            }

            is SuperContext -> {
                return node.build().also {
                    contexts[ContextInfo(node.name, node.type)] = node
                }
            }

            is SuperCommandGroup -> {
                val data = CommandDataImpl(node.name, node.description)
                val commands = node.commands()
                val groups = node.groups()

                if (groups != null) {
                    data.addSubcommandGroups(
                        groups.map {g ->
                            nextSubNode(node.name, g)
                        }
                    )
                } else if (commands != null) {
                    data.addSubcommands(
                        commands.map {cmd ->
                            listeners[Info(node.name, null, cmd.name)] = cmd

                            cmd.buildSub()
                        }
                    )
                }

                return data
            }
        }
    }

    private fun parse(): List<CommandData> {
        val built = ArrayList<CommandData>()

        for (node in nodes) {
            built += nextNode(node)
        }

        return built
    }

    override fun init(jda: JDA) {
        val commands = parse()

        jda.updateCommands().addCommands(commands).queue()

        jda.addEventListener(EventListener())
    }

    inner class EventListener : ListenerAdapter() {
        override fun onMessageContextInteraction(event: MessageContextInteractionEvent) {
            val info = ContextInfo(event.name, event.commandType)

            contexts[info]?.run(event)
        }

        override fun onUserContextInteraction(event: UserContextInteractionEvent) {
            val info = ContextInfo(event.name, event.commandType)

            contexts[info]?.run(event)
        }

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