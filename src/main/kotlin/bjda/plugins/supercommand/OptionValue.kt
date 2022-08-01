package bjda.plugins.supercommand

import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

typealias AnyOption = IOptionValue<*, *>

open class OptionValueMapper<T, R>(
    private val base: IOptionValue<T, *>,
    val mapper: (T) -> R
): IOptionValue<R, OptionValueMapper<T, R>> {
    override var data: OptionData by base::data

    override fun parseMapping(mapping: OptionMapping?): R {
        val parsed = base.parseMapping(mapping)
        return mapper(parsed)
    }
}

open class OptionValue<T>(
    name: String,
    type: OptionType,
    description: String) : IOptionValue<T, OptionValue<T>> {

    override var data = OptionData(type, name, description)
    var default: (() -> T)? = null

    fun optional(): OptionValue<T?> = this as OptionValue<T?>

    fun optional(default: () -> T): OptionValue<T> {

        return this.required(false).default(default)
    }

    fun default(value: () -> T): OptionValue<T> {
        default = value
        return this
    }

    override fun parseMapping(mapping: OptionMapping?): T {
        if (mapping == null) {
            return default?.invoke()?: null as T
        }

        val data: Any = when (mapping.type) {
            OptionType.INTEGER -> mapping.asLong
            OptionType.NUMBER -> mapping.asDouble
            OptionType.BOOLEAN -> mapping.asBoolean
            OptionType.STRING -> mapping.asString
            OptionType.ATTACHMENT -> mapping.asAttachment
            //Role, User, Channels are all mentionable
            OptionType.MENTIONABLE, OptionType.ROLE, OptionType.CHANNEL, OptionType.USER -> mapping.asMentionable
            else -> error("Unknown option type ${mapping.type}")
        }

        return data as T
    }
}

interface IOptionValue<T, O: IOptionValue<T, O>> {
    var data: OptionData

    private val self
        get() = this as O

    fun parseMapping(mapping: OptionMapping?): T

    infix fun value(event: SlashCommandInteractionEvent): T {
        val mapping = event.getOption(data.name)

        return parseMapping(mapping)
    }

    fun<R> map(value: (T) -> R): OptionValueMapper<T, R> {

        return OptionValueMapper(this, value)
    }

    fun required(value: Boolean = true): O {
        data = data.setRequired(value)

        return self
    }

    fun localizeName(vararg lang: Pair<DiscordLocale, String>) = localizeName(
        mapOf(*lang)
    )

    fun localizeDescription(vararg lang: Pair<DiscordLocale, String>) = localizeDescription(
        mapOf(*lang)
    )

    fun localizeName(map: Map<DiscordLocale, String>): O {
        data.setNameLocalizations(map)
        return self
    }

    fun localizeDescription(map: Map<DiscordLocale, String>): O {
        data.setDescriptionLocalizations(map)
        return self
    }

    fun autoComplete(value: Boolean): O {
        data = data.setAutoComplete(value)

        return self
    }

    fun intRange(range: Pair<Long, Long>): O {
        val (min, max) = range
        data.setRequiredRange(min, max)

        return self
    }

    fun doubleRange(range: Pair<Double, Double>): O {
        val (min, max) = range
        data.setRequiredRange(min, max)

        return self
    }

    fun channel(vararg types: ChannelType) = channel(types.toList())

    fun channel(types: Collection<ChannelType>): O {
        if (data.type != OptionType.CHANNEL)
            error("Option Type must be channel")

        data.setChannelTypes(types)

        return self
    }

    fun choices(vararg choices: Command.Choice) = choices(
        choices.toList()
    )

    fun<V> choices(vararg choice: Pair<String, V>) = choices(
        choice.map {(key, value) ->
            choice(key, value)
        }
    )

    fun choices(choices: Collection<Command.Choice>): O {
        data.addChoices(choices)

        return self
    }

    companion object {

        infix fun<T> SlashCommandInteractionEvent.value(option: IOptionValue<T, *>): T {
            return option value this
        }

        fun<V> choice(key: String, value: V): Command.Choice {
            return when (value) {
                is Long -> Command.Choice(key, value)
                is Double -> Command.Choice(key, value)
                else -> Command.Choice(key, value.toString())
            }
        }
    }
}