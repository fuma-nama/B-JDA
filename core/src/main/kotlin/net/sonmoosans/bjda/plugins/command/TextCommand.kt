package net.sonmoosans.bjda.plugins.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.ProcessedArgument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import com.github.ajalt.clikt.parameters.options.convert
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

abstract class TextCommand(
    help: String = "",
    epilog: String = "",
    name: String? = null,
    invokeWithoutSubcommand: Boolean = false,
    printHelpOnEmptyArgs: Boolean = false,
    helpTags: Map<String, String> = emptyMap(),
    autoCompleteEnvvar: String? = "",
    allowMultipleSubcommands: Boolean = false,
    treatUnknownOptionsAsArgs: Boolean = false,
    hidden: Boolean = false,
) : CliktCommand(
    help, epilog, name, invokeWithoutSubcommand, printHelpOnEmptyArgs, helpTags, autoCompleteEnvvar, allowMultipleSubcommands, treatUnknownOptionsAsArgs
) {
    val event by requireObject<MessageReceivedEvent>()

    fun<T: IMentionable> OptionWithValues<String?, String, String>.mention(type: Message.MentionType): OptionWithValues<T?, T, T> {
        return this.convert {id->
            findMention(type, id) as T
        }
    }

    fun<T: IMentionable> ProcessedArgument<String, String>.mention(type: Message.MentionType): ProcessedArgument<T, T> {
        return convert{id->
            findMention(type, id) as T
        }
    }

    internal fun findMention(type: Message.MentionType, mention: String): IMentionable? {
        return event.message.mentions.getMentions(type).find {
            it.asMention == mention
        }
    }
}