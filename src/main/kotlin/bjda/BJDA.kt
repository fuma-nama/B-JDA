package bjda

import bjda.plugins.IModule
import net.dv8tion.jda.api.JDA
import java.util.*

class BJDA private constructor(private val jda: JDA) {
    private val modules = ArrayList<IModule>()

    companion object {
        fun create(jda: JDA, init: BJDA.() -> Unit = {}): BJDA {
            return BJDA(jda).apply(init)
        }
    }

    fun install(vararg modules: IModule): BJDA {
        for (module in modules) {

            this.modules.add(module)
            module.init(jda)
        }

        return this
    }

    fun uninstall(module: IModule): BJDA {
        modules.remove(module)
        return this
    }
}