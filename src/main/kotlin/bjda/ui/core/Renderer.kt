package bjda.ui.core

import bjda.ui.types.AnyElement
import java.util.*
import kotlin.collections.ArrayList

typealias UpdateTask = () -> Payload
data class Payload(val comp: AnyElement, val afterUpdate: (() -> Unit)? = null)

abstract class Renderer {
    private val updateQueue: Queue<UpdateTask> = LinkedList()
    private var rendering = false

    abstract fun onUpdated()

    fun isRendering(): Boolean {
        return rendering
    }

    abstract fun getScanner(): ComponentTreeScanner

    /**
     * If any render task is added when rendering component
     * It will also be rendered here
     */
    fun renderElement(comp: AnyElement) {
        val snapshot = comp.snapshot
        var rendered = comp.render()

        if (rendered != null) {

            rendered = getScanner().scan(comp, snapshot, rendered)

            for (child in rendered) {
                if (child != null) {
                    renderElement(child)
                }
            }
        }

        comp.snapshot = rendered
    }

    private fun executeUpdateQueue() {
        rendering = true
        val listeners = ArrayList<() -> Unit>()

        while (updateQueue.isNotEmpty()) {
            val payload = updateQueue.peek().invoke()

            renderElement(payload.comp)

            payload.afterUpdate?.let(listeners::add)

            updateQueue.poll()
        }
        rendering = false

        listeners.forEach {
            it.invoke()
        }
    }

    @Synchronized
    fun addUpdateTask(task: UpdateTask) {
        updateQueue.offer(task)

        if (!rendering) {
            executeUpdateQueue()
        }
    }
}