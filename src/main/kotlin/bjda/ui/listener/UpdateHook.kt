package bjda.ui.listener

import net.dv8tion.jda.api.entities.Message

interface UpdateHook {
    val id: String
    fun onUpdate(message: Message)
    fun onDestroy()
}