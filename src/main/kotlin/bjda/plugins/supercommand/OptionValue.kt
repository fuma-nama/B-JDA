package bjda.plugins.supercommand

import bjda.plugins.supercommand.builder.choice
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

class OptionValue<T>(
    name: String,
    type: OptionType,
    description: String) : IOptionValue<T, OptionValue<T>> {

    override var data = OptionData(type, name, description)
    var default: (() -> T)? = null

    var autoComplete
        get() = data.isAutoComplete
        set(v) {
            data.isAutoComplete = v
        }

    var required
        get() = data.isRequired
        set(v) {
            data.isRequired = v
        }

    fun optional(): OptionValue<T?> = this.required(false) as OptionValue<T?>

    fun optional(default: () -> T): OptionValue<T> {

        return this.required(false).default(default)
    }

    fun default(value: () -> T): OptionValue<T> {
        default = value
        return this
    }

    fun choices(vararg choices: Pair<String, T>) = choices(
        choices.map { (key, value) ->
            choice(key, value)
        }
    )

    fun choices(vararg choices: Command.Choice) = choices(
        choices.toList()
    )

    fun choices(choices: Collection<Command.Choice>): OptionValue<T> {
        data.addChoices(choices)

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

    infix fun valueOf(event: SlashCommandInteractionEvent): T {
        val mapping = event.getOption(data.name)

        return parseMapping(mapping)
    }

    fun<R> map(value: (T) -> R): OptionValueMapper<T, R> {

        return OptionValueMapper(this, value)
    }

    fun<R> map(value: (T) -> R, init: O.() -> Unit): OptionValueMapper<T, R> {
        self.apply(init)

        return OptionValueMapper(this, value)
    }

    fun required(value: Boolean = true): O {
        data = data.setRequired(value)

        return self
    }

    fun name(locale: DiscordLocale, name: String): O {
        data.setNameLocalization(locale, name)

        return self
    }

    fun description(locale: DiscordLocale, description: String): O {
        data.setDescriptionLocalization(locale, description)

        return self
    }

    fun names(vararg lang: Pair<DiscordLocale, String>) = names(
        mapOf(*lang)
    )

    fun descriptions(vararg lang: Pair<DiscordLocale, String>) = descriptions(
        mapOf(*lang)
    )

    fun names(map: Map<DiscordLocale, String>): O {
        data.setNameLocalizations(map)
        return self
    }

    fun descriptions(map: Map<DiscordLocale, String>): O {
        data.setDescriptionLocalizations(map)
        return self
    }

    fun autoComplete(value: Boolean): O {
        data = data.setAutoComplete(value)

        return self
    }

    companion object {
        infix fun<T> SlashCommandInteractionEvent.valueOf(option: IOptionValue<T, *>): T {
            return option valueOf this
        }
    }
}