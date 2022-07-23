package bjda.plugins.supercommand

import bjda.plugins.supercommand.entries.PermissionEntry
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.internal.interactions.CommandDataImpl
import java.awt.Color

abstract class SuperCommand (
    override val name: String,
    val description: String = "No Description",
    override val guildOnly: Boolean? = null,
    override val permissions: DefaultMemberPermissions? = null
): SuperNode, PermissionEntry, NameLocalization, DescriptionLocalization {
    lateinit var event: SlashCommandInteractionEvent
    private val options = ArrayList<IOptionValue<*>>()

    open fun<T> option(type: OptionType, name: String, description: String = "No Description"): OptionValue<T> {
        val value = OptionValue<T>(name, type, description)
        options.add(value)

        return value
    }

    internal fun execute(event: SlashCommandInteractionEvent) {
        this.event = event

        for (option in options) {
            option.onUpdate(event)
        }

        try {
            run()
        } catch (e: Throwable) {
            error(e.message)
        }
    }

    open fun error(message: String?) {
        event.replyEmbeds(
            EmbedBuilder()
                .setTitle(message?: "ERROR")
                .setColor(Color.RED)
                .build()
        ).setEphemeral(true).queue()
    }

    abstract fun run()

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
}