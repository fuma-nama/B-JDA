package bjda.plugins.supercommand.entries

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.internal.interactions.CommandDataImpl

interface PermissionEntry {
    val guildOnly: Boolean?
    val permissions: DefaultMemberPermissions?

    fun CommandDataImpl.setPermissions(): CommandDataImpl {
        guildOnly?.let {
            setGuildOnly(it)
        }
        permissions?.let {
            setDefaultPermissions(it)
        }

        return this
    }
}