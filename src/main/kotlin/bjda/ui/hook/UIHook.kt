package bjda.ui.hook

import bjda.ui.core.UI
import net.dv8tion.jda.api.entities.Message

interface UIHook {
    fun onEnable(ui: UI) = Unit

    /**
     * UI Hooks will be destroyed while its attached UI being destroyed
     *
     * Or it can be destroyed by manually call onDestroy method
     */
    fun onDestroy()
}