package bjda.plugins.ui.modal

import bjda.plugins.ui.UIEvent
import bjda.plugins.ui.hook.event.ModalListener
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.Modal

/**
 * A pool to store modals and modal listeners.
 *
 * When a modal is submitted, its listeners will be destroyed
 */
class ModalPool(val creator: ModalCreator) {

    /**
     * Open a new modal
     *
     * Notice that id cannot be duplicated
     */
    fun next(id: String = UIEvent.createId(), onSubmit: ModalListener): Modal {
        UIEvent.listen(id, PoolModalListener(id, onSubmit))

        return creator(id)
    }

    fun destroy(id: String) {
        UIEvent.modals.remove(id)
    }

    inner class PoolModalListener(val id: String, private val listener: ModalListener) : ModalListener {
        override fun onSubmit(event: ModalInteractionEvent) {
            listener.onSubmit(event)

            destroy(id)
        }
    }
}