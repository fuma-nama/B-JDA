package bjda

import bjda.plugins.IModule
import bjda.wrapper.BJDABuilder
import bjda.wrapper.Mode
import net.dv8tion.jda.api.JDA
import java.util.*

fun bjda(mode: Mode, init: BJDABuilder.() -> Unit): BJDA {
    return BJDABuilder(mode).apply(init).ready()
}

class BJDA(private val jda: JDA) {
    private val modules = ArrayList<IModule>()

    companion object {
        fun create(jda: JDA, init: BJDA.() -> Unit = {}): BJDA {
            return BJDA(jda).apply(init)
        }
    }

    fun install(vararg modules: IModule): BJDA {

        return install(modules.toList())
    }

    fun install(modules: Collection<IModule>): BJDA {
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