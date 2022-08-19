package bjda.plugins.supercommand.builder

import bjda.plugins.supercommand.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import java.util.*

typealias OptionInit<T> = OptionValue<T, T>.() -> Unit
typealias NumberOptionInit<T> = NumberOption<T, T>.() -> Unit

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

class SuperCommandBuilder(val base: SuperCommandImpl): OptionBuilder by base {
    fun name(local: DiscordLocale, name: String) {
        base.localNames[local] = name
    }

    fun description(local: DiscordLocale, name: String) {
        base.localNames[local] = name
    }
    
    fun execute(listener: CommandHandler) {
        base.run = listener
    }

    fun execute(scope: CoroutineScope, listener: suspend EventContext.() -> Unit) {
        base.run = {
            scope.launch {
                listener()
            }
        }
    }
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