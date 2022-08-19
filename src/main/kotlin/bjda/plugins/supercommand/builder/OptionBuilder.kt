package bjda.plugins.supercommand.builder

import bjda.plugins.supercommand.NumberOption
import bjda.plugins.supercommand.OptionValue
import bjda.utils.DslBuilder
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType

fun<O: OptionValue<out Number, *>> O.range(range: Pair<Number?, Number?>): O {
    val (min, max) = range

    if (min != null) {
        when (min) {
            is Double -> data.setMinValue(min)
            is Float -> data.setMinValue(min.toDouble())
            else -> data.setMinValue(min.toLong())
        }
    }

    if (max != null) {
        when (max) {
            is Double -> data.setMaxValue(max)
            is Float -> data.setMinValue(max.toDouble())
            else -> data.setMaxValue(max.toLong())
        }
    }

    return this
}

fun<O: OptionValue<V, *>, V: Channel> O.channel(vararg types: ChannelType) = channel(types.toList())

fun<O: OptionValue<V, *>, V: Channel> O.channel(types: Collection<ChannelType>): O {
    if (data.type != OptionType.CHANNEL)
        error("Option Type must be channel")

    data.setChannelTypes(types)

    return this
}

@DslBuilder
class ChoicesBuilder<out T> {
    val choices = arrayListOf<Command.Choice>()
}

fun ChoicesBuilder<String>.choice(key: String, value: String) {
    choices += Command.Choice(key, value)
}

fun ChoicesBuilder<Number>.choice(key: String, value: Number) {
    choices += when (value) {
        is Double -> Command.Choice(key, value)
        is Float -> Command.Choice(key, value.toDouble())
        else -> Command.Choice(key, value.toLong())
    }
}

interface OptionBuilder {
    fun addOption(option: OptionValue<*, *>)

    fun<T: Any> option(
        type: OptionType,
        name: String,
        description: String,
        init: (OptionValue<T, T>.() -> Unit)? = null
    ): OptionValue<T, T> {
        val value = OptionValue<T, T>(name, type, description)

        if (init != null) {
            value.apply(init)
        }

        addOption(value)
        return value
    }

    fun<T: Channel> channel(name: String, description: String, vararg types: ChannelType) = option<T>(
        OptionType.CHANNEL, name, description
    ).channel(*types)

    fun int(name: String, description: String, init: NumberOptionInit<Int>? = null) = number(
        name, description, init
    )

    fun long(name: String, description: String, init: NumberOptionInit<Long>? = null) = number(
        name, description, init
    )

    fun double(name: String, description: String, init: NumberOptionInit<Double>? = null) = number(
        name, description, init
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

inline fun<reified T: Number> OptionBuilder.number(
    name: String,
    description: String,
    noinline init: (NumberOption<T>.() -> Unit)? = null
): NumberOption<T> {
    val value = NumberOption(name, description, T::class)

    if (init != null) {
        value.apply(init)
    }

    addOption(value)
    return value
}