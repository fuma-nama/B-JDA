package net.sonmoosans.bjda.ui.hook

import net.sonmoosans.bjda.ui.core.UI

interface UIHook {
    fun onEnable(ui: UI) = Unit

    /**
     * UI Hooks will be destroyed while its attached UI being destroyed
     *
     * Or it can be destroyed by manually call onDestroy method
     */
    fun onDestroy()
}