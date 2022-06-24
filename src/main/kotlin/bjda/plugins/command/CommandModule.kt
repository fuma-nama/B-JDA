package bjda.plugins.command

import bjda.plugins.IModule
import com.github.ajalt.clikt.core.CliktCommand
import net.dv8tion.jda.api.JDA

class CommandModule(vararg commands: BJDACommand) : CommandListener(commands), IModule {
    override var prefix: String = "!"

    override fun init(jda: JDA) {
        jda.addEventListener(this)
    }
}