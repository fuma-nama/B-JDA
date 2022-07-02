package bjda.plugins.ui

import bjda.plugins.IModule
import net.dv8tion.jda.api.JDA

class UIEventModule : IModule {
    override fun init(jda: JDA) {
        jda.addEventListener(UIEvent())
    }
}