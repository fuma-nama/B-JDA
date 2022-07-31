package bjda.plugins.ui.modal

import bjda.plugins.ui.UIEvent
import bjda.plugins.ui.hook.event.ModalListener
import bjda.ui.component.row.Row
import bjda.ui.core.hooks.IHook
import bjda.ui.types.AnyComponent
import bjda.utils.LambdaList
import bjda.utils.build
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