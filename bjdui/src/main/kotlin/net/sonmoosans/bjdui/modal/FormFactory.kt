package net.sonmoosans.bjdui.modal

import net.sonmoosans.bjda.plugins.ui.UIEvent
import net.sonmoosans.bjda.plugins.ui.hook.event.ModalListener
import net.sonmoosans.bjdui.component.row.Row
import net.sonmoosans.bjda.ui.core.hooks.IHook
import net.sonmoosans.bjdui.types.AnyComponent
import net.sonmoosans.bjda.utils.LambdaList
import net.sonmoosans.bjda.utils.build
import net.dv8tion.jda.api.interactions.components.Modal

abstract class FormFactory(val id: String = UIEvent.createId()): IHook<Modal>, ModalListener {
    abstract val title: String
    abstract fun render(): LambdaList<Row>

    fun listen() {
        UIEvent.listen(id, this)
    }

    fun create(): Modal {
        val rows = render().build().map {
            it.build()
        }

        return Modal.create(id, title).addActionRows(rows).build()
    }

    override fun onCreate(component: AnyComponent, initial: Boolean): Modal {
        if (initial) {
            listen()
        }

        return create()
    }

    override fun onDestroy() {
        UIEvent.modals.remove(id)
    }
}