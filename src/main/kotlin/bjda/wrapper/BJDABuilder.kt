package bjda.wrapper

import bjda.BJDA
import bjda.plugins.IModule
import kotlinx.coroutines.coroutineScope
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent

class BJDABuilder(mode: Mode) {
    private val modules = ArrayList<IModule>()

    var builder = when (mode) {
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
        return builder.apply(init)
    }

    fun listen(listener: ListenerAdapter) {
        builder.addEventListeners(listener)
    }

    suspend fun ready(): BJDA = coroutineScope {
        val jda = builder.build().awaitReady()

        BJDA.create(jda).install(modules)
    }
}

enum class Mode {
    Light, Default, Full
}