package net.sonmoosans.bjdui.plugin.reaction

import net.sonmoosans.bjda.plugins.IModule
import net.sonmoosans.bjda.wrapper.BJDABuilder
import net.dv8tion.jda.api.JDA

fun BJDABuilder.reaction() {
    + ReactionEventModule()
}
class ReactionEventModule : IModule {

    override fun init(jda: JDA) {
        jda.addEventListener(ReactionEvent())
    }
}