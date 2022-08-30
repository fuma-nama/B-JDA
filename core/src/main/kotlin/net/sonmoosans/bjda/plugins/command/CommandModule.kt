package net.sonmoosans.bjda.plugins.command

import net.sonmoosans.bjda.plugins.IModule
import net.dv8tion.jda.api.JDA

class CommandModule(vararg commands: TextCommand) : CommandListener(commands), IModule {
    override var prefix: String = "!"

    override fun init(jda: JDA) {
        jda.addEventListener(this)
    }
}