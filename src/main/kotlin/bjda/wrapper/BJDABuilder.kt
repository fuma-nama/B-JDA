package bjda.wrapper

import bjda.BJDA
import bjda.plugins.IModule
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent

class BJDABuilder(mode: Mode)  {
    private val modules = ArrayList<IModule>()
    private var listener: (BJDA.() -> Unit)? = null

    var mode = when (mode) {
        Mode.Light -> JDABuilder.createLight(null)
        Mode.Default -> JDABuilder.createDefault(null)
        Mode.Full -> JDABuilder.create(null,
            GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)
        )
    }

    operator fun IModule.unaryPlus() {
        modules += this
    }

    operator fun IModule.unaryMinus() {
        modules.remove(this)
    }

    fun install(modules: Collection<IModule>) {
        this.modules.addAll(modules)
    }

    fun uninstall(modules: Collection<IModule>) {
        this.modules.removeAll(modules.toSet())
    }

    inline fun config(init: JDABuilder.() -> Unit): JDABuilder {
        return mode.apply(init)
    }

    fun onReady(listener: BJDA.() -> Unit) {
        this.listener = listener
    }

    internal fun ready(): BJDA {
        val jda = mode.build().awaitReady()

        return BJDA.create(jda)
            .install(modules)
            .also {
                listener?.let {l -> it.apply(l) }
            }
    }
}

enum class Mode {
    Light, Default, Full
}