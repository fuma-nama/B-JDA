package bjda.plugins.command

import bjda.plugins.IModule
import net.dv8tion.jda.api.JDA

class CommandModule : IModule {
    override fun init(jda: JDA) {
        jda.addEventListener()
    }
}