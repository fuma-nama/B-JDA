package bjda.plugins.ui.hook

import bjda.ui.core.UI
import bjda.ui.core.hooks.IHook
import bjda.ui.types.AnyComponent

/**
 * Create an id and listener to listen for an event
 *
 * Then return the ID
 */
abstract class EventHook(val id: String) : IHook<String> {
    lateinit var ui: UI

    override fun onCreate(component: AnyComponent): String {
        this.ui = component.ui

        return id
    }
}