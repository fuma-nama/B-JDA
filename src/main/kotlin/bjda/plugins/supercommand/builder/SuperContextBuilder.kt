package bjda.plugins.supercommand.builder

import bjda.plugins.supercommand.SuperContext
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import java.util.*

fun userCommand(
    name: String,
    guildOnly: Boolean? = null,
    permissions: DefaultMemberPermissions? = null,
    init: SuperContextBuilder<UserContextInteractionEvent>.() -> Unit

): SuperContext<UserContextInteractionEvent> {

    val builder = SuperContextBuilder<UserContextInteractionEvent>(
        SuperContextImpl(name, Command.Type.USER, guildOnly, permissions)
    )

    builder.apply(init)

    return builder.base
}

fun messageCommand(
    name: String,
    guildOnly: Boolean? = null,
    permissions: DefaultMemberPermissions? = null,
    init: SuperContextBuilder<MessageContextInteractionEvent>.() -> Unit

): SuperContextImpl<MessageContextInteractionEvent> {

    val builder = SuperContextBuilder<MessageContextInteractionEvent> (
        SuperContextImpl(name, Command.Type.MESSAGE, guildOnly, permissions)
    )

    builder.apply(init)

    return builder.base
}

class SuperContextBuilder<T : GenericContextInteractionEvent<*>>(val base: SuperContextImpl<T>) {
    fun name(locale: DiscordLocale, name: String) {
        base.localNames[locale] = name
    }

    fun execute(listener: (T) -> Unit) {
        base.run = listener
    }
}

class SuperContextImpl<T: GenericContextInteractionEvent<*>>(
    name: String,
    type: Command.Type,
    guildOnly: Boolean?,
    permissions: DefaultMemberPermissions?
) : SuperContext<T>(name, type, guildOnly, permissions) {
    override var localNames = EnumMap<DiscordLocale, String>(DiscordLocale::class.java)

    lateinit var run: (T) -> Unit

    override fun run(event: T) = this.run.invoke(event)
}