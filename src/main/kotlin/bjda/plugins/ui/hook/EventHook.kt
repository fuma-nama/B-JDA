package bjda.plugins.ui.hook

import bjda.ui.core.hooks.IHook
import bjda.ui.types.AnyComponent

abstract class EventHook(private val id: String): IHook<String> {

    override fun onCreate(component: AnyComponent, initial: Boolean): String {
        if (initial) {
            listen(id)
        }

        return id
    }

    override fun onDestroy() {
        destroy(id)
    }

    abstract fun listen(id: String)

    abstract fun destroy(id: String)
}