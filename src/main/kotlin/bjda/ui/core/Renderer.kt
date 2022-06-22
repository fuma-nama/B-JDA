package bjda.ui.core

import bjda.ui.types.AnyComponent
import java.util.*

typealias UpdateTask = () -> AnyComponent
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
        while (updateQueue.isNotEmpty()) {
            val task = updateQueue.peek()

            renderComponent(task())
            updateQueue.poll()
        }
        rendering = false

        onUpdated()
    }

    @Synchronized
    fun addUpdateTask(task: UpdateTask) {
        updateQueue.offer(task)

        if (!rendering) {
            executeUpdateQueue()
        }
    }
}