package net.sonmoosans.bjda.ui.core.internal

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

open class UpdaterImpl : Updater {
    private var current: RestAction<*>? = null
    private var next: RestAction<*>? = null

    override fun addTask(action: RestAction<*>) {
        if (current == null) {
            current = action

            update()
        } else {
            next = action
        }
    }

    private fun update() {
        current?.queue {
            current = next

            update()
        }
    }
}