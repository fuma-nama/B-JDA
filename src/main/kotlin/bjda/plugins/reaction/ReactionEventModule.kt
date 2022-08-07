package bjda.plugins.reaction

import bjda.plugins.IModule
import bjda.wrapper.BJDABuilder
import net.dv8tion.jda.api.JDA

fun BJDABuilder.reaction() {
    + ReactionEventModule()
}
class ReactionEventModule : IModule {

    override fun init(jda: JDA) {
        jda.addEventListener(ReactionEvent())
    }
}