package bjda.ui.core

import bjda.utils.LambdaBuilder
import java.util.*

typealias UpdateTask = () -> FComponent
abstract class Renderer(private val scanner: ComponentTreeScanner) {
    private val updateQueue: Queue<UpdateTask> = LinkedList()
    private var rendering = false

    abstract fun onUpdated()

    fun isRendering(): Boolean {
        return rendering
    }

    /**
     * If any render task is added when rendering component
     * It will also be rendered here
     */
    fun<S : Any> renderComponent(comp: Component<*, S>) {
        val snapshot = comp.children
        val rendered = comp.render()?.let { LambdaBuilder.build(it).toTypedArray() }

        val scanned = scanner.scan(snapshot, rendered)

        scanned?.forEach {child ->
            if (child != null)
                renderComponent(child)
        }

        comp.children = scanned
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