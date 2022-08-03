package bjda.plugins.supercommand.entries

import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData
import net.dv8tion.jda.internal.interactions.CommandDataImpl
import java.util.*

typealias LocalizeMap = Map<DiscordLocale, String>

fun localizes() = EnumMap<DiscordLocale, String>(DiscordLocale::class.java)

interface CommandLocalization {
    val localNames: LocalizeMap?
        get() = null

    fun CommandDataImpl.setLocalize(): CommandDataImpl {
        return localNames?.let {
            setNameLocalizations(it)
        }?: this
    }

    fun SubcommandData.setLocalize(): SubcommandData {
        return localNames?.let {
            setNameLocalizations(it)
        }?: this
    }
}

interface SlashLocalization : CommandLocalization {

    val localDescriptions: LocalizeMap?
        get() = null

    override fun CommandDataImpl.setLocalize(): CommandDataImpl {
        localNames?.let {
            setNameLocalizations(it)
        }
        
        localDescriptions?.let {
            setDescriptionLocalizations(it)
        }
        
        return this
    }
    
    fun SubcommandGroupData.setLocalize(): SubcommandGroupData {
        localNames?.let {
            setNameLocalizations(it)
        }
        
        localDescriptions?.let {
            setDescriptionLocalizations(it)
        }
        
        return this
    }

    override fun SubcommandData.setLocalize(): SubcommandData {

        localNames?.let {
            setNameLocalizations(it)
        }

        localDescriptions?.let {
            setDescriptionLocalizations(it)
        }
        
        return this
    }
}