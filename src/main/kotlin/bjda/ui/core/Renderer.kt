package bjda.ui.core

import bjda.ui.types.AnyElement
import java.util.*

typealias UpdateTask = () -> AnyElement
abstract class Renderer() {
    private val updateQueue: Queue<UpdateTask> = LinkedList()
    private var rendering = false

    abstract fun onUpdated()

    fun isRendering(): Boolean {
        return rendering
    }

    abstract fun createScanner(element: AnyElement): ComponentTreeScanner

    /**
     * If any render task is added when rendering component
     * It will also be rendered here
     */
    fun renderElement(element: AnyElement) {
        val snapshot = element.elements
        val scanned = createScanner(element)
            .scan(snapshot, element.render())

        scanned.forEach {child ->
            if (child != null) {
                renderElement(child)
            }
        }

        element.elements = scanned
    }

    private fun executeUpdateQueue() {
        rendering = true
        while (updateQueue.isNotEmpty()) {
            val task = updateQueue.peek()

            renderElement(task())
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