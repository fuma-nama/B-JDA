package bjda.plugins.supercommand.entries

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.CommandData

interface PermissionEntry {
    val guildOnly: Boolean?
    val permissions: DefaultMemberPermissions?

    fun CommandData.setPermissions(): CommandData {
        guildOnly?.let {
            setGuildOnly(it)
        }
        permissions?.let {
            setDefaultPermissions(it)
        }

        return this
    }
}