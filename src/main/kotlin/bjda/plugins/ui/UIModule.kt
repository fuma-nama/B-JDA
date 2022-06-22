package bjda.plugins.ui

import bjda.plugins.IModule
import net.dv8tion.jda.api.JDA

class UIModule : IModule {
    override fun init(jda: JDA) {
        jda.addEventListener(UIEvent())
    }
}