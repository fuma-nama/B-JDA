package bjda

import net.dv8tion.jda.api.JDA

interface IModule {
    fun init(jda: JDA)
}