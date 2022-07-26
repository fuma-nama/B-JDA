package bjda.plugins.ui.hook

import bjda.ui.core.hooks.IHook
import bjda.ui.types.AnyComponent

abstract class EventHook(val id: String): IHook<String> {

    override fun onCreate(component: AnyComponent, initial: Boolean): String {
        if (initial) {
            listen()
        }

        return id
    }

    abstract override fun onDestroy()

    abstract fun listen()
}