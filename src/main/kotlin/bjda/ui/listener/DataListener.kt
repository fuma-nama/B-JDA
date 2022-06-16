package bjda.ui.listener

import net.dv8tion.jda.api.entities.Message

interface DataListener {
    fun onUpdate(message: Message)
    fun onDestroy()
}