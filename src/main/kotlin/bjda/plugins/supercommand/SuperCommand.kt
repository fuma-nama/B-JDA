package bjda.plugins.supercommand

import bjda.plugins.supercommand.builder.OptionBuilder
import bjda.plugins.supercommand.entries.SlashLocalization
import bjda.plugins.supercommand.entries.PermissionEntry
import bjda.plugins.supercommand.entries.SuperNode
import bjda.utils.embed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.internal.interactions.CommandDataImpl
import java.awt.Color

typealias CommandHandler = EventContext.() -> Unit

abstract class SuperCommand (
    override val name: String,
    val description: String = "No Description",
    override val guildOnly: Boolean? = null,
    override val permissions: DefaultMemberPermissions? = null
): SuperNode, PermissionEntry, SlashLocalization, OptionBuilder {
    private val options = ArrayList<AnyOption>()

    override fun<T> option(
        type: OptionType,
        name: String,
        description: String,
        init: (OptionValue<T>.() -> Unit)?
    ): OptionValue<T> {
        val value = OptionValue<T>(name, type, description)

        if (init != null) {
            value.apply(init)
        }

        options.add(value)
        return value
    }

    internal fun execute(event: SlashCommandInteractionEvent) {
        val info = EventContext(event)

        try {
            run(info)
        } catch (e: Throwable) {
            info.error(e.message)
        }
    }

    abstract val run: CommandHandler

    override fun build(listeners: Listeners): CommandDataImpl {
        val data = CommandDataImpl(name, description)
            .setPermissions()
            .setLocalize()

        data.addOptions(options.map {
            it.data
        })

        listeners[Info(name = name)] = this

        return data
    }

    open fun buildSub(group: String, subgroup: String? = null, listeners: Listeners): SubcommandData {
        val data = SubcommandData(name, description)
            .setLocalize()

        data.addOptions(options.map {
            it.data
        })

        listeners[Info(group, subgroup, name)] = this

        return data
    }
}

class EventContext(
    val event: SlashCommandInteractionEvent
) {

    operator fun<T, P> IOptionValue<T, *>.getValue(parent: P, property: Any): T {
        return valueOf(event)
    }

    fun<T> IOptionValue<T, *>.value(): T {
        return valueOf(event)
    }

    operator fun<T> IOptionValue<T, *>.invoke(): T {
        return valueOf(event)
    }

    fun error(message: String?) {
        event.replyEmbeds(
            embed(
                title = message?: "Error",
                color = Color.RED
            )
        ).setEphemeral(true).queue()
    }
}