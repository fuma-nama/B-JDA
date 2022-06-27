package bjda.plugins.supercommand

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.internal.interactions.CommandDataImpl
import java.awt.Color

abstract class SuperCommand(
    val group: String? = null,
    val subgroup: String? = null,
    val name: String,
    val description: String) {
    lateinit var event: SlashCommandInteractionEvent
    private val options = ArrayList<OptionValue>()

    fun option(type: OptionType, name: String, description: String = "No Description"): OptionValue {
        val value = OptionValue(name, type, description)
        options.add(value)

        return value
    }

    internal fun execute(event: SlashCommandInteractionEvent) {
        this.event = event

        for (option in options) {
            option.update(event)
        }

        try {
            run()
        } catch (e: Throwable) {
            error(e.message)
        }
    }

    fun error(message: String?) {
        event.replyEmbeds(
            EmbedBuilder()
                .setTitle(message?: "ERROR")
                .setColor(Color.RED)
                .build()
        ).queue()
    }

    abstract fun run()

    internal fun build(): CommandDataImpl {
        val data = CommandDataImpl(name, description)

        data.addOptions(options.map {
            it.data
        })

        return data
    }

    internal fun buildSub(): SubcommandData {
        val data = SubcommandData(name, description)

        data.addOptions(options.map {
            it.data
        })

        return data
    }

    class OptionValue(
        name: String,
        type: OptionType,
        description: String) {

        private var value: Any? = null
        internal var data = OptionData(type, name, description)

        fun required(value: Boolean): OptionValue {
            data = data.setRequired(value)

            return this
        }

        fun autoComplete(value: Boolean): OptionValue {
            data = data.setAutoComplete(value)
            return this
        }

        fun intRange(range: Pair<Long, Long>): OptionValue {
            val (min, max) = range
            data.setRequiredRange(min, max)

            return this
        }

        fun doubleRange(range: Pair<Double, Double>): OptionValue {
            val (min, max) = range
            data.setRequiredRange(min, max)

            return this
        }

        fun channel(vararg types: ChannelType): OptionValue {
            if (data.type != OptionType.CHANNEL)
                error("Option Type must be channel")

            data.setChannelTypes(*types)

            return this
        }

        fun choices(vararg choice: Pair<String, String>): OptionValue {
            data.addChoices(choice.map {(key, value)->
                Choice(key, value)
            })

            return this
        }

        fun update(event: SlashCommandInteractionEvent) {
            val mapping = event.getOption(data.name)

            value = if (mapping == null) {
                null
            } else {
                when (mapping.type) {
                    OptionType.INTEGER -> mapping.asLong
                    OptionType.NUMBER -> mapping.asDouble
                    OptionType.BOOLEAN -> mapping.asBoolean
                    OptionType.STRING -> mapping.asString
                    OptionType.ATTACHMENT -> mapping.asAttachment
                    //Role, User, Channels are all mentionable
                    OptionType.MENTIONABLE, OptionType.ROLE, OptionType.CHANNEL, OptionType.USER -> mapping.asMentionable
                    else -> error("Unknown option type ${mapping.type}")
                }
            }
        }

        operator fun<T> getValue(parent: Any, property: Any): T {
            return value as T
        }
    }
}