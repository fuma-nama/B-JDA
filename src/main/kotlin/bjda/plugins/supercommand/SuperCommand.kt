package bjda.plugins.supercommand

import bjda.plugins.supercommand.entries.DescriptionLocalization
import bjda.plugins.supercommand.entries.NameLocalization
import bjda.plugins.supercommand.entries.PermissionEntry
import bjda.plugins.supercommand.entries.SuperNode
import bjda.utils.embed
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.internal.interactions.CommandDataImpl
import java.awt.Color
import java.nio.channels.Channel

typealias CommandHandler = EventInfo.() -> Unit

abstract class SuperCommand (
    override val name: String,
    val description: String = "No Description",
    override val guildOnly: Boolean? = null,
    override val permissions: DefaultMemberPermissions? = null
): SuperNode, PermissionEntry, NameLocalization, DescriptionLocalization {
    private val options = ArrayList<IOptionValue<*>>()

    open fun<T> option(type: OptionType, name: String, description: String = "No Description"): OptionValue<T> {
        val value = OptionValue<T>(name, type, description)

        options.add(value)
        return value
    }

    fun<T: Channel> channel(name: String, description: String, vararg types: ChannelType): OptionValue<T> {
        return option<T>(OptionType.CHANNEL, name, description).channel(*types)
    }

    internal fun execute(event: SlashCommandInteractionEvent) {
        val info = EventInfo(event)

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
            .setLocalName()
            .setLocalDescription()

        data.addOptions(options.map {
            it.data
        })

        listeners[Info(name = name)] = this

        return data
    }

    open fun buildSub(group: String, subgroup: String? = null, listeners: Listeners): SubcommandData {
        val data = SubcommandData(name, description)
            .setLocalName()
            .setLocalDescription()

        data.addOptions(options.map {
            it.data
        })

        listeners[Info(group, subgroup, name)] = this

        return data
    }

    companion object {
        val int = wrap<Int>(OptionType.INTEGER)
        val number = wrap<Double>(OptionType.NUMBER)
        val user = wrap<User>(OptionType.USER)
        val role = wrap<Role>(OptionType.ROLE)
        val text = wrap<String>(OptionType.STRING)
        val boolean = wrap<Boolean>(OptionType.BOOLEAN)
        val mentionable = wrap<IMentionable>(OptionType.MENTIONABLE)

        private fun<T> wrap(type: OptionType): SuperCommand.(name: String, description: String) -> OptionValue<T> {
            return {name, description ->

                option(type, name, description)
            }
        }
    }
}

class EventInfo(
    val event: SlashCommandInteractionEvent
) {

    operator fun<T, P> IOptionValue<T>.getValue(parent: P, property: Any): T {
        return value(event)
    }

    fun<T> IOptionValue<T>.value(): T {
        return value(event)
    }

    operator fun<T> IOptionValue<T>.invoke(): T {
        return value(event)
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