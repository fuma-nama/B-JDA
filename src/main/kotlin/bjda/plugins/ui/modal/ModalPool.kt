package bjda.plugins.ui.modal

import bjda.plugins.ui.UIEvent
import bjda.plugins.ui.hook.event.ModalListener
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.Modal

interface ModalPool {
    /**
     * Create a Listener
     *
     * @see next
     */
    fun listen(id: String = UIEvent.createId(), listener: ModalListener): PoolModalListener {
        return PoolModalListener(this, id, listener)
    }

    /**
     * Open a new modal
     */
    fun next(listener: PoolModalListener): Modal

    /**
     * Open a new modal
     */
    fun next(id: String = UIEvent.createId(), listener: ModalListener) = next(
        listen(id, listener)
    )

    fun destroy(id: String)

    /**
     * Destroy all listeners
     */
    fun destroyAll()

    class PoolModalListener(val parent: ModalPool, val id: String, private val listener: ModalListener) : ModalListener {
        override fun onSubmit(event: ModalInteractionEvent) {
            listener.onSubmit(event)

            parent.destroy(id)
        }
    }

    companion object {
        /**
         * Create a modal pool
         */
        fun single(creator: ModalCreator): ModalPoolImpl {
            return ModalPoolImpl(creator)
        }

        /**
         * Create a modal pool that supports duplicated ids
         */
        fun multi(creator: ModalCreator): ModalPoolMulti {
            return ModalPoolMulti(creator)
        }

        fun fixed(id: String = UIEvent.createId(), creator: ModalCreator): FixedModalPool {
            return FixedModalPool(id, creator)
        }

        fun fixed(id: String = UIEvent.createId(), creator: ModalCreator, listener: ModalListener): FixedModalPool {
            val pool = FixedModalPool(id, creator)
            pool.listen(listener)

            return pool
        }
    }
}

/**
 * A Fixed ID pool to store modals with only one modal listeners.
 *
 * When no modal is waiting for submit, its listener will be temporarily destroyed.
 */
open class FixedModalPool(id: String, val creator: ModalCreator) {
    private var info = Info(id)

    fun listen(listener: ModalListener) {

        UIEvent.listen(info.id, Listener(listener))
    }

    fun destroy() {
        info.used--

        if (info.used == 0) {
            println("destroy")
            UIEvent.modals.remove(info.id)
        }
    }

    fun next(): Modal {
        info.used++

        return creator(info.id)
    }

    data class Info(val id: String, var used: Int = 0)

    inner class Listener(private val base: ModalListener): ModalListener {
        override fun onSubmit(event: ModalInteractionEvent) {
            base.onSubmit(event)

            destroy()
        }
    }
}

/**
 * A pool to store modals and modal listeners.
 *
 * When a modal is submitted, its listener will be destroyed
 */
open class ModalPoolImpl(val creator: ModalCreator): ModalPool {
    private val ids = HashSet<String>()

    /**
     * Open a new modal
     *
     * Notice that id cannot be duplicated
     */
    override fun next(listener: ModalPool.PoolModalListener): Modal {
        val id = listener.id

        UIEvent.listen(id, listener)
        ids += id

        return creator(id)
    }

    override fun destroy(id: String) {
        UIEvent.modals.remove(id)
        ids.remove(id)
    }

    override fun destroyAll() {
        for (id in ids) {
            UIEvent.modals.remove(id)
        }

        ids.clear()
    }
}

/**
 * A pool to store modals and modal listeners, which supports duplicated ids.
 *
 * When all modals with the same id are submitted, its listener will be destroyed
 */
class ModalPoolMulti(val creator: ModalCreator): ModalPool {
    private val ids = HashMap<String, Int>()

    /**
     * Open a new modal, Id can be duplicated
     */
    override fun next(listener: ModalPool.PoolModalListener): Modal {
        val id = listener.id
        val using = ids.getOrDefault(id, 0)

        if (using == 0) {
            UIEvent.listen(id, listener)
        }

        ids[id] = using + 1

        return creator(id)
    }

    override fun destroy(id: String) {
        val using = ids[id]?.minus(1)
            ?: return

        if (using == 0) {
            UIEvent.modals.remove(id)
            ids.remove(id)
        }

        ids[id] = using
    }

    /**
     * Destroy the listener even the id is still using
     */
    fun forceDestroy(id: String) {
        UIEvent.modals.remove(id)

        ids.remove(id)
    }

    override fun destroyAll() {
        for (id in ids.keys) {
            UIEvent.modals.remove(id)
        }

        ids.clear()
    }
}