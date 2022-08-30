package net.sonmoosans.bjda.plugins.ui.hook

import net.sonmoosans.bjda.ui.core.hooks.IHook
import net.sonmoosans.bjdui.types.AnyComponent

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