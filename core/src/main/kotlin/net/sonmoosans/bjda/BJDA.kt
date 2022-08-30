package net.sonmoosans.bjda

import net.sonmoosans.bjda.plugins.IModule
import net.sonmoosans.bjda.wrapper.BJDABuilder
import net.sonmoosans.bjda.wrapper.Mode
import kotlinx.coroutines.coroutineScope
import net.dv8tion.jda.api.JDA
import java.util.*

suspend fun bjda(mode: Mode, init: BJDABuilder.() -> Unit): BJDA = coroutineScope {
    BJDABuilder(mode).apply(init).ready()
}

class BJDA(val jda: JDA) {
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