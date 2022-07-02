package bjda.plugins.reaction

import bjda.plugins.IModule
import net.dv8tion.jda.api.JDA

class ReactionEventModule : IModule {

    override fun init(jda: JDA) {
        jda.addEventListener(ReactionEvent())
    }
}