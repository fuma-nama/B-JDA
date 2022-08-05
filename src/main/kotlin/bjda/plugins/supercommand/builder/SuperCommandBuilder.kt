package bjda.plugins.supercommand.builder

import bjda.plugins.supercommand.CommandHandler
import bjda.plugins.supercommand.OptionValue
import bjda.plugins.supercommand.SuperCommand
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.util.*

typealias OptionInit<T> = OptionValue<T>.() -> Unit

fun command(
    name: String,
    description: String,
    guildOnly: Boolean? = null,
    permissions: DefaultMemberPermissions? = null,
    init: SuperCommandBuilder.() -> Unit
): SuperCommand {
    val builder = SuperCommandBuilder(
        SuperCommandImpl(
            name, description, guildOnly, permissions
        )
    )
    
    builder.apply(init)
    
    return builder.base
}

class SuperCommandBuilder(val base: SuperCommandImpl): OptionBuilder {
    fun name(local: DiscordLocale, name: String) {
        base.localNames[local] = name
    }

    fun description(local: DiscordLocale, name: String) {
        base.localNames[local] = name
    }
    
    fun execute(listener: CommandHandler) {
        base.run = listener
    }

    //options
    override fun<T> option(type: OptionType, name: String, description: String, init: OptionInit<T>?) = base.option(
        type, name, description, init
    )
}

open class SuperCommandImpl(
    name: String,
    description: String,
    guildOnly: Boolean?,
    permissions: DefaultMemberPermissions?,
) : SuperCommand(name, description, guildOnly, permissions) {
    
    override var localNames = EnumMap<DiscordLocale, String>(DiscordLocale::class.java)
    override var localDescriptions = EnumMap<DiscordLocale, String>(DiscordLocale::class.java)
    
    override lateinit var run: CommandHandler
}