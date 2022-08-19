package bjda.plugins.supercommand

import bjda.plugins.supercommand.builder.ChoicesBuilder
import bjda.utils.DslBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import kotlin.reflect.KClass

typealias AnyOption = IOptionValue<*, *, *>

open class OptionValueMapper<T: Any, N, R>(
    private val base: IOptionValue<T, N, *>,
    val mapper: (N) -> R
): IOptionValue<T, R, OptionValueMapper<T, N, R>> {
    override var data: OptionData by base::data

    override fun parseMapping(mapping: OptionMapping?): R {
        val parsed = base.parseMapping(mapping)
        return mapper(parsed)
    }
}

open class NumberOption<T: Number>(
    name: String,
    description: String,
    private val original: KClass<T>
): OptionValue<T, T>(
    name,
    when (original) {
        Int::class, Long::class -> OptionType.INTEGER
        else -> OptionType.NUMBER
    },
    description
) {
    override fun parseMapping(mapping: OptionMapping?): T {
        if (mapping == null) {
            return default?.invoke()?: null as T
        }

        val data = when (mapping.type) {
            OptionType.INTEGER -> mapping.asLong
            OptionType.NUMBER -> mapping.asDouble
            else -> error("Unknown option type ${mapping.type}")
        }

        return when (original) {
            Int::class -> data.toInt()
            Float::class -> data.toFloat()
            else -> data
        } as T
    }
}

open class OptionValue<T: Any, R>(
    name: String,
    type: OptionType,
    description: String) : IOptionValue<T, R, OptionValue<T, R>> {

    override var data = OptionData(type, name, description)
    var default: (() -> R & Any)? = null

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

    fun optional() = this.required(false) as OptionValue<T, R?>

    fun optional(default: () -> R & Any): OptionValue<T, R & Any> {

        return this.required(false).default(default)
    }

    fun default(value: () -> R & Any): OptionValue<T, R & Any> {
        default = value
        return this as OptionValue<T, R & Any>
    }

    override fun parseMapping(mapping: OptionMapping?): R {
        if (mapping == null) {
            return default?.invoke()?: null as R
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

        return data as R
    }
}

@DslBuilder
interface IOptionValue<T: Any, R, O: IOptionValue<T, R, O>> {
    var data: OptionData

    private val self
        get() = this as O

    fun parseMapping(mapping: OptionMapping?): R

    infix fun valueOf(event: SlashCommandInteractionEvent): R {
        val mapping = event.getOption(data.name)

        return parseMapping(mapping)
    }

    fun<N> map(value: (R) -> N) = OptionValueMapper(this, value)
    fun<N> map(value: (R) -> N, apply: O.() -> Unit) = OptionValueMapper(self.apply(apply), value)

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

    fun choices(choices: ChoicesBuilder<T>.() -> Unit) = choices(
        ChoicesBuilder<T>().apply(choices).choices
    )

    fun choices(vararg choices: Command.Choice) = choices(
        choices.toList()
    )

    fun choices(choices: Collection<Command.Choice>): O {
        data.addChoices(choices)

        return self
    }

    companion object {
        infix fun<R> SlashCommandInteractionEvent.valueOf(option: IOptionValue<*, R, *>): R {
            return option valueOf this
        }
    }
}