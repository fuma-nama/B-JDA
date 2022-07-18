package bjda.ui.hook

import bjda.ui.core.UI
import net.dv8tion.jda.api.entities.Message

interface UIHook {
    fun onEnable(ui: UI) = Unit
    fun onDestroy()
}