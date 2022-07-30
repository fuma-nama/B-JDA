package bjda.plugins.supercommand.entries

import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.internal.interactions.CommandDataImpl

typealias LocalizeMap = Map<DiscordLocale, String>

interface NameLocalization {
    val localNames: LocalizeMap?
        get() = null

    fun CommandDataImpl.setLocalName(): CommandDataImpl {
        return localNames?.let {
            setNameLocalizations(it)
        }?: this
    }

    fun SubcommandData.setLocalName(): SubcommandData {
        return localNames?.let {
            setNameLocalizations(it)
        }?: this
    }
}

interface DescriptionLocalization {
    val localDescriptions: LocalizeMap?
        get() = null

    fun CommandDataImpl.setLocalDescription(): CommandDataImpl {

        return localDescriptions?.let {
            setDescriptionLocalizations(it)
        }?: this
    }

    fun SubcommandData.setLocalDescription(): SubcommandData {

        return localDescriptions?.let {
            setDescriptionLocalizations(it)
        }?: this
    }
}