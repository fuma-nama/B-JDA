package bjda.plugins.command

import bjda.ui.exceptions.UnexpectedTypeException
import bjda.plugins.command.listener.CommandHandler
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction

class CommandCompiler {
    //Id: command handler
    private val commands = CompiledRoutes()

    fun queue(action: CommandCreateAction, handler: CommandHandler) {
        action.queue { cmd ->
            println("Declared Command: " + cmd.name)
            register(cmd.id, handler)
        }
    }

    fun compile(container: IGroup): CommandHandler {
        when (container) {
            is ParsedNestedGroup -> {
                val map = hashMapOf<String, CompiledGroup>()

                for (group in container.groups) {
                    map[group.name] = compile(group) as CompiledGroup
                }
                return CompiledNestedGroup(map)
            }
            is ParsedGroup -> {
                val map = hashMapOf<String, CompiledCommand>()

                for (command in container.commands) {
                    map[command.name] = CompiledCommand(command)
                }
                return CompiledGroup(map)
            }
            else -> throw throw UnexpectedTypeException(container::class)
        }
    }

    fun register(id: String, handler: CommandHandler) {
        commands[id] = handler
    }

    fun getResult(): CompiledRoutes {
        return commands
    }

    data class CompiledCommand(val command: ParsedCommand) : CommandHandler {
        override fun handle(event: SlashCommandInteractionEvent) {
            command.handler.handle(event)
        }
    }

    data class CompiledGroup(val commands: HashMap<String, CompiledCommand>) : CommandHandler {
        override fun handle(event: SlashCommandInteractionEvent) {
            commands[event.subcommandName]?.handle(event)
        }
    }

    data class CompiledNestedGroup(val groups: HashMap<String, CompiledGroup>) : CommandHandler {
        override fun handle(event: SlashCommandInteractionEvent) {
            groups[event.subcommandGroup]?.handle(event)
        }
    }

}

typealias CompiledRoutes = HashMap<String, CommandHandler>
