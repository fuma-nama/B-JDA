package bjda.plugins.supercommand.builder

import bjda.plugins.supercommand.IOptionValue
import bjda.plugins.supercommand.OptionValue
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType

fun<O: OptionValue<V>, V: Channel> O.channel(vararg types: ChannelType) = channel(types.toList())

fun<O: OptionValue<V>, V: Channel> O.channel(types: Collection<ChannelType>): O {
    if (data.type != OptionType.CHANNEL)
        error("Option Type must be channel")

    data.setChannelTypes(types)

    return this
}

fun<V> choice(key: String, value: V): Command.Choice {
    return when (value) {
        is Long -> Command.Choice(key, value)
        is Int -> Command.Choice(key, value.toLong())
        is Double -> Command.Choice(key, value)
        else -> Command.Choice(key, value.toString())
    }
}

fun<V: Number, O: IOptionValue<V, O>> O.range(range: Pair<V?, V?>): O {
    val (min, max) = range

    if (min != null) {
        when (min) {
            is Long -> data.setMinValue(min)
            is Double -> data.setMinValue(min)
            else -> data.setMinValue(min.toLong())
        }
    }

    if (max != null) {
        when (max) {
            is Long -> data.setMaxValue(max)
            is Double -> data.setMaxValue(max)
            else -> data.setMaxValue(max.toLong())
        }
    }

    return this
}

interface OptionBuilder {
    fun<T> option(
        type: OptionType,
        name: String,
        description: String,
        init: (OptionValue<T>.() -> Unit)? = null
    ): OptionValue<T>

    fun<T: Channel> channel(name: String, description: String, vararg types: ChannelType): OptionValue<T> {
        return option<T>(OptionType.CHANNEL, name, description).channel(*types)
    }

    @Deprecated("Please use long instead", ReplaceWith("long(name, description, init)"))
    fun int(name: String, description: String, init: OptionInit<Long>? = null) = long(
        name, description, init
    ).map { it.toInt() }

    fun long(name: String, description: String, init: OptionInit<Long>? = null) = option(
        OptionType.INTEGER, name, description, init
    )

    fun number(name: String, description: String, init: OptionInit<Double>? = null) = option(
        OptionType.NUMBER, name, description, init
    )

    fun text(name: String, description: String, init: OptionInit<String>? = null) = option(
        OptionType.STRING, name, description, init
    )

    fun attachment(name: String, description: String, init: OptionInit<Message.Attachment>? = null) = option(
        OptionType.ATTACHMENT, name, description, init
    )

    fun<C: Channel> channel(name: String, description: String, init: OptionInit<C>? = null) = option(
        OptionType.CHANNEL, name, description, init
    )

    fun boolean(name: String, description: String, init: OptionInit<Boolean>? = null) = option(
        OptionType.BOOLEAN, name, description, init
    )

    fun user(name: String, description: String, init: OptionInit<User>? = null) = option(
        OptionType.USER, name, description, init
    )

    fun member(name: String, description: String, init: OptionInit<Member>? = null) = option(
        OptionType.USER, name, description, init
    )

    fun role(name: String, description: String, init: OptionInit<Role>? = null) = option(
        OptionType.ROLE, name, description, init
    )

    fun mentionable(name: String, description: String, init: OptionInit<IMentionable>? = null) = option(
        OptionType.MENTIONABLE, name, description, init
    )
}