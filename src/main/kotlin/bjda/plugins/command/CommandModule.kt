package bjda.plugins.command

import bjda.IModule
import bjda.ui.exceptions.UnexpectedTypeException
import bjda.plugins.command.annotations.Command
import bjda.plugins.command.annotations.CommandGroup
import bjda.plugins.command.annotations.Param
import bjda.plugins.command.exceptions.IllegalGroupException
import bjda.plugins.command.listener.CommandHandler
import bjda.plugins.command.listener.CommandListener
import bjda.utils.notEmpty
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.*

fun createInstance(controller: KClass<*>): Any {
    return controller.createInstance()
}

class CommandModule(private vararg val controllers: KClass<*>) : IModule {
    override fun init(jda: JDA) {
        val groups = Stack<IGroup>()
        val commands = Stack<ParsedCommand>()

        fun scanController(controller: KClass<*>) {
            val group = controller.findAnnotation<CommandGroup>()
            val parser = CommandParser.from(controller)

            if (group != null) {
                val parsed = parser.parseToGroup(group)
                groups.add(parsed)
            } else {
                commands.addAll(
                    parser.parseMethods()
                )
                groups.addAll(
                    parser.parseSubgroups()
                )
            }
        }

        for (controller in controllers) {
            scanController(controller)
        }

        val compiled = CommandBuilder(groups, commands, CommandCompiler(), jda).declareAll()
        jda.addEventListener(CommandListener(compiled))
    }
}

class CommandBuilder(private val groups: Stack<IGroup>,
                     private val commands: Stack<ParsedCommand>,
                     private val compiler: CommandCompiler,
                     private val jda: JDA) {

    fun declareAll(): CompiledRoutes {
        for (group in groups) {
            declareGroup(group)
        }

        for (command in commands) {
            declareCommand(command)
        }

        return compiler.getResult()
    }

    private fun declareGroup(group: IGroup) {
        val builder = jda.upsertCommand(group.name, group.description)

        when (group) {
            is ParsedGroup -> {
                builder.addSubcommands(buildSubCommands(group))
            }
            is ParsedNestedGroup -> {
                builder.addSubcommandGroups(buildSubGroup(group))
            }
            else -> throw UnexpectedTypeException(group::class)
        }

        compiler.queue(
            builder,
            compiler.compile(group)
        )
    }

    private fun declareCommand(command: ParsedCommand) {
        compiler.queue(
            jda.upsertCommand(command.name, command.description)
                .addOptions(buildOptions(command)),
            command.handler
        )
    }

    private fun buildSubCommands(container: ParsedGroup): List<SubcommandData> {

        return container.commands.map { command ->
            SubcommandData(command.name, command.description)
                .addOptions(buildOptions(command))
        }
    }

    private fun buildOptions(command: ParsedCommand): List<OptionData> {
        return command.options
            .filterIsInstance<ParsedOption>()
            .map {option -> option.data }
    }

    private fun buildSubGroup(nestedGroup: ParsedNestedGroup): Stack<SubcommandGroupData> {
        val groups = Stack<SubcommandGroupData>()

        for (group in nestedGroup.groups) {
            val groupData = SubcommandGroupData(group.name, group.description)

            groupData.addSubcommands(buildSubCommands(group))

            groups.push(groupData)
        }

        return groups
    }
}

class CommandParser private constructor (private val controller: Any) {
    private val type = controller::class

    companion object {
        fun from(type: KClass<*>): CommandParser {
            return CommandParser(createInstance(type))
        }
    }

    fun parseToGroup(info: CommandGroup): IGroup {
        val commands = parseMethods()
        val groups = parseSubgroups()

        if (notEmpty(commands, groups)) {
            throw IllegalGroupException("Mixed group is not supported", type)
        }

        if (commands.isNotEmpty()) {
            return ParsedGroup(info.name, info.description, commands)
        }

        if (groups.isNotEmpty()) {
            return ParsedNestedGroup(info.name, info.description, groups)
        }

        throw IllegalGroupException("Empty group is not supported", type)
    }

    fun parseSubgroups(): List<ParsedGroup> {
        return type.nestedClasses
            .filter {it.hasAnnotation<CommandGroup>()}
            .map { child ->
                val subGroup = child.findAnnotation<CommandGroup>()!!

                val parsed = from(child).parseToGroup(subGroup)

                if (parsed is ParsedGroup) {
                    parsed
                } else {
                    throw IllegalArgumentException("Sub Group can only contains commands")
                }
            }
    }

    fun parseMethods(): List<ParsedCommand> {
        return type.functions
            .filter {it.hasAnnotation<Command>()}
            .map {function ->
            val info = function.findAnnotation<Command>()!!
            val options = function.valueParameters.map { parseOption(it) }.toTypedArray()
            val handler = ControllerHandler(controller, function, options)

            ParsedCommand(info.name, info.description, options, handler)
        }
    }

    private fun parseOption(param: KParameter): IOption {
        val data = param.findAnnotation<Param>()

        return if (data == null) {
            ParsedLocalOption(EventMapper(param))
        } else {
            val info = OptionParser.from(data, param)
            ParsedOption(info, OptionMapper(info))
        }
    }
}

class ControllerHandler(private val controller: Any, private val function: KFunction<*>, private val options: Array<IOption>) : CommandHandler {
    override fun handle(event: SlashCommandInteractionEvent) {
        val args = options.map {
            it.creator.create(event)
        }.toTypedArray()

        function.call(controller, *args)
    }
}

interface IGroup {
    val name: String
    val description: String
}

interface IOption {
    val creator: ValueCreator
}

class ParsedCommand(val name: String, val description: String, val options: Array<IOption>, val handler: CommandHandler)

data class ParsedGroup(override val name: String,
                       override val description: String,
                       val commands: List<ParsedCommand>): IGroup

data class ParsedNestedGroup(override val name: String,
                             override val description: String,
                             val groups: List<ParsedGroup>): IGroup

data class ParsedOption(val data: OptionData, override val creator: ValueCreator) : IOption

data class ParsedLocalOption(override val creator: ValueCreator): IOption