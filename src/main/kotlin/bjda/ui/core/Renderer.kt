package bjda.ui.core

import bjda.ui.types.AnyComponent
import java.util.*
import kotlin.collections.ArrayList

typealias UpdateTask = () -> Payload
data class Payload(val comp: AnyComponent, val afterUpdate: (() -> Unit)? = null)

abstract class Renderer {
    private val updateQueue: Queue<UpdateTask> = LinkedList()
    private var rendering = false

    abstract fun onUpdated()

    fun isRendering(): Boolean {
        return rendering
    }

    abstract fun createScanner(element: AnyComponent): ComponentTreeScanner

    /**
     * If any render task is added when rendering component
     * It will also be rendered here
     */
    fun renderComponent(comp: AnyComponent) {
        val snapshot = comp.snapshot
        val scanned = createScanner(comp)
            .scan(snapshot, comp.render())

        scanned.forEach {child ->
            if (child != null) {
                renderComponent(child)
            }
        }

        comp.snapshot = scanned
    }

    private fun executeUpdateQueue() {
        rendering = true
        val listeners = ArrayList<() -> Unit>()

        while (updateQueue.isNotEmpty()) {
            val payload = updateQueue.peek().invoke()

            renderComponent(payload.comp)

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