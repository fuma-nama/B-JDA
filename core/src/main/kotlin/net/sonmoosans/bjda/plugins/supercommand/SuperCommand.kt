package net.sonmoosans.bjda.plugins.supercommand

import net.sonmoosans.bjda.plugins.supercommand.builder.OptionBuilder
import net.sonmoosans.bjda.plugins.supercommand.entries.SlashLocalization
import net.sonmoosans.bjda.plugins.supercommand.entries.PermissionEntry
import net.sonmoosans.bjda.plugins.supercommand.entries.SuperNode
import net.sonmoosans.bjda.utils.embed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
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

    override fun addOption(option: OptionValue<*, *>) {
        options.add(option)
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
    val<T> IOptionValue<*, T, *>.value
        get() = from(event)

    operator fun<T> IOptionValue<*, T, *>.invoke(): T {
        return from(event)
    }

    operator fun<T, R> IOptionValue<*, T, *>.invoke(mapper: T.() -> R): R {
        return from(event).let(mapper)
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