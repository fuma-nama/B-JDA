package bjda.ui

import bjda.ui.core.*
import bjda.ui.listener.DataListener
import net.dv8tion.jda.api.entities.Message
import java.util.*
typealias Task = () -> Unit
class ComponentManager(private val root: FComponent) {
    private val scanner = TreeScanner(this)
    private val listeners = Stack<DataListener>()
    private val updateQueue: Queue<Task> = LinkedList()

    /**
     State has two types:
     Global State and private state,

     Global State is stored in manager itself which can be shared to multi components.
     Private state used to store single component data which is only shared to the component and its children.
     **/
    init {
        root.onMount(this)
        renderComponent(root)
    }

    fun build(): Message {
        val data = RenderData()
        root.build(data)

        return data.build()
    }

    /**
     * If any render task is added when rendering component
     * It will also be rendered here
     */
    private fun<S> renderComponent(comp: Component<*, S>) {
        val snapshot = comp.children
        val rendered = comp.render()
        val scanned = scanner.scan(snapshot, rendered) //Can invoke updateComponent()

        scanned?.forEach {child ->
            if (child != null)
                renderComponent(child)
        }

        comp.children = scanned
    }

    fun updateMessage() {
        val message = build()

        for (listener in listeners) {
            listener.onUpdate(message)
        }
    }

    private fun executeUpdateQueue() {

        while (updateQueue.isNotEmpty()) {
            val task = updateQueue.peek()
            task()

            updateQueue.poll()
        }

        updateMessage()
    }

    /**
     * Render the component and its children
     *
     * And then update the message after rendering
     */
    @Synchronized
    fun<S> updateComponent(component: Component<*, S>, state: S) {
        val rendering = updateQueue.isNotEmpty()

        updateQueue.offer {
            component.state = state

            renderComponent(component)
        }

        if (!rendering) {
            executeUpdateQueue()
        }
    }

    fun listen(entity: DataListener) {
        listeners.push(entity)
    }

    fun destroy() {
        listeners.forEach { it.onDestroy() }
    }
}