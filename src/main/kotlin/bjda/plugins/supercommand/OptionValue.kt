package bjda.plugins.supercommand

import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class OptionValueMapper<T, R>(
    private val base: IOptionValue<T>,
    val mapper: (T) -> R) {

    operator fun getValue(parent: Any, property: Any): R {
        return mapper(base.value)
    }
}

open class OptionValue<T>(
    name: String,
    type: OptionType,
    description: String) : IOptionValue<T> {

    override var value: T = null as T
    override var data = OptionData(type, name, description)
    var default: (() -> T)? = null

    fun required(value: Boolean): OptionValue<T> {
        data = data.setRequired(value)

        return this
    }

    fun localizeName(map: Map<DiscordLocale, String>): OptionValue<T> {
        data.setNameLocalizations(map)
        return this
    }

    fun localizeName(vararg lang: Pair<DiscordLocale, String>): OptionValue<T> {
        return this.localizeName(mapOf(*lang))
    }

    fun localizeDescription(map: Map<DiscordLocale, String>): OptionValue<T> {
        data.setDescriptionLocalizations(map)
        return this
    }

    fun localizeDescription(vararg lang: Pair<DiscordLocale, String>): OptionValue<T> {
        return localizeDescription(mapOf(*lang))
    }

    fun autoComplete(value: Boolean): OptionValue<T> {
        data = data.setAutoComplete(value)
        return this
    }

    fun intRange(range: Pair<Long, Long>): OptionValue<T> {
        val (min, max) = range
        data.setRequiredRange(min, max)

        return this
    }

    fun doubleRange(range: Pair<Double, Double>): OptionValue<T> {
        val (min, max) = range
        data.setRequiredRange(min, max)

        return this
    }

    fun channel(vararg types: ChannelType): OptionValue<T> {
        if (data.type != OptionType.CHANNEL)
            error("Option Type must be channel")

        data.setChannelTypes(*types)

        return this
    }

    fun choices(vararg choice: Pair<String, String>): OptionValue<T> {
        data.addChoices(choice.map {(key, value)->
            Command.Choice(key, value)
        })

        return this
    }

    fun<R> map(value: (T) -> R): OptionValueMapper<T, R> {
        return OptionValueMapper(this, value)
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

interface IOptionValue<T> {
    var value: T
    val data: OptionData

    fun parseMapping(mapping: OptionMapping?): T

    fun onUpdate(event: SlashCommandInteractionEvent) {
        val mapping = event.getOption(data.name)

        value = parseMapping(mapping)
    }

    operator fun getValue(parent: Any, property: Any): T {
        return value
    }

    operator fun getValue(parent: Nothing?, property: Any): T {
        return value
    }
}