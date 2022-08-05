package bjda.plugins.supercommand

import bjda.plugins.IModule
import bjda.plugins.supercommand.entries.SuperNode
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class Listeners {
    private val commands = HashMap<Info, SuperCommand>()
    private val contexts = HashMap<ContextInfo, SuperContext<*>>()

    fun run(info: Info, event: SlashCommandInteractionEvent) {
        commands[info]?.execute(event)
    }

    fun run(info: ContextInfo, event: GenericContextInteractionEvent<*>) {
        contexts[info]?.call(event)
    }

    private fun<T : GenericContextInteractionEvent<*>> SuperContext<T>.call(event: GenericContextInteractionEvent<*>) {
        this.run(event as T)
    }

    operator fun set(info: ContextInfo, context: SuperContext<*>) {
        contexts[info] = context
    }

    operator fun set(info: Info, command: SuperCommand) {
        commands[info] = command
    }
}

data class ContextInfo(val name: String, val type: Command.Type)

data class Info(val group: String? = null, val subgroup: String? = null, val name: String)

open class SuperCommandModule(
    /**
     * Commands to register
     */
    vararg val nodes: SuperNode,
    /**
     * Guilds which allow to use commands when global is false
     */
    val guilds: Array<String>? = null,
    /**
     * If false, only specified guilds will be able to use commands
     */
    val global: Boolean = true
) : IModule {
    val listeners = Listeners()

    private fun parse(): List<CommandData> {
        return nodes.map {node ->
            node.build(listeners)
        }
    }

    override fun init(jda: JDA) {
        val commands = parse()

        if (global) {
            jda.updateCommands().addCommands(commands).queue()
        } else {
            guilds?.forEach { guild ->
                jda.getGuildById(guild)
                    ?.updateCommands()
                    ?.addCommands(commands)
                    ?.queue()
            }
        }

        jda.addEventListener(EventListener())
    }

    inner class EventListener : ListenerAdapter() {
        override fun onMessageContextInteraction(event: MessageContextInteractionEvent) {
            val info = ContextInfo(event.name, event.commandType)

            listeners.run(info, event)
        }

        override fun onUserContextInteraction(event: UserContextInteractionEvent) {
            val info = ContextInfo(event.name, event.commandType)

            listeners.run(info, event)
        }

        override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
            val info = if (event.subcommandName != null) {
                Info(event.name, event.subcommandGroup, event.subcommandName!!)
            } else {
                Info(null, null, event.name)
            }
            listeners.run(info, event)
        }
    }
}