package bjda.ui.core.internal

import net.dv8tion.jda.api.requests.RestAction
import java.util.function.Consumer

interface Updater {
    fun addTask(action: RestAction<*>)

    fun<T> addTask(action: RestAction<T>, success: Consumer<T>?) {
        addTask(
            if (success == null) action else action.map(success::accept)
        )
    }
}