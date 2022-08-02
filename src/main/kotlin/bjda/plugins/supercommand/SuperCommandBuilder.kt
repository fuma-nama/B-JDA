package bjda.plugins.supercommand

import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.entities.Message.Attachment
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.util.*

typealias OptionInit<T> = OptionValue<T>.() -> Unit

fun command(
    name: String,
    description: String = "No Description",
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

class SuperCommandBuilder(val base: SuperCommandImpl) {
    fun name(local: DiscordLocale, name: String) {
        base.localNames[local] = name
    }

    fun description(local: DiscordLocale, name: String) {
        base.localNames[local] = name
    }
    
    fun execute(listener: CommandHandler) {
        base.run = listener
    }
    
    fun<T> option(type: OptionType, name: String, description: String, init: OptionInit<T>? = null) = base.option(
        type, name, description, init
    )
    
    //options
    fun int(name: String, description: String, init: OptionInit<Int>? = null) = option(
        OptionType.INTEGER, name, description, init
    )

    fun long(name: String, description: String, init: OptionInit<Long>? = null) = option(
        OptionType.INTEGER, name, description, init
    )
    
    fun number(name: String, description: String, init: OptionInit<Double>? = null) = option(
        OptionType.NUMBER, name, description, init
    )
    
    fun text(name: String, description: String, init: OptionInit<String>? = null) = option(
        OptionType.STRING, name, description, init
    )
    
    fun attachment(name: String, description: String, init: OptionInit<Attachment>? = null) = option(
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