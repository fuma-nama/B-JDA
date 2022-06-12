package bjda

import net.dv8tion.jda.api.JDA
import java.util.*

class BJDA private constructor(private val jda: JDA) {
    private val modules = ArrayList<IModule>()

    companion object {
        fun create(jda: JDA): BJDA {
            return BJDA(jda)
        }
    }

    fun install(module: IModule): BJDA {
        modules.add(module)
        module.init(jda)

        return this
    }

    fun uninstall(module: IModule): BJDA {
        modules.remove(module)
        return this
    }
}