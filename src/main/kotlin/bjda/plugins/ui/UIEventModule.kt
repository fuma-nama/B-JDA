package bjda.plugins.ui

import bjda.plugins.IModule
import bjda.wrapper.BJDABuilder
import net.dv8tion.jda.api.JDA

fun BJDABuilder.uiEvent() {
    + UIEventModule()
}

class UIEventModule : IModule {
    override fun init(jda: JDA) {
        jda.addEventListener(UIEvent())
    }
}