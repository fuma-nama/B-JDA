package bjda.plugins.ui.hook

import bjda.ui.core.hooks.IHook
import bjda.ui.types.AnyComponent

abstract class EventHook(val id: String): IHook<String> {
    override fun onCreate(component: AnyComponent): String {
        return id
    }

    abstract fun destroy()
}