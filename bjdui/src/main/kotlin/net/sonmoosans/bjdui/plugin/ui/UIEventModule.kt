package net.sonmoosans.bjda.plugins.ui

import net.sonmoosans.bjda.plugins.IModule
import net.sonmoosans.bjda.wrapper.BJDABuilder
import net.dv8tion.jda.api.JDA

fun BJDABuilder.uiEvent() {
    + UIEventModule()
}

class UIEventModule : IModule {
    override fun init(jda: JDA) {
        jda.addEventListener(UIEvent())
    }
}