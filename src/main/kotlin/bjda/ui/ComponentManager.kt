package bjda.ui

import bjda.ui.core.FComponent
import bjda.ui.core.RenderData
import bjda.ui.core.TreeScanner
import net.dv8tion.jda.api.entities.Message
import java.util.*

class ComponentManager(private val child: FComponent) {
    val scanner = TreeScanner(this)
    private val listeners = Stack<DataListener>()
    /**
     State has two types:
     Global State and private state,

     Global State is stored in manager itself which can be shared to multi components.
     Private state used to store single component data which is only shared to the component and its children.
     **/
    init {
        child.onMount(this)
    }

    fun build(): Message {
        val data = RenderData()
        child.build(data)
        return data.build()
    }

    fun update() {
        val message = build()

        for (listener in listeners) {
            listener.onUpdate(message)
        }
    }

    fun listen(entity: DataListener) {
        listeners.push(entity)
    }

    fun destroy() {
        listeners.forEach { it.onDestroy() }
    }

    interface DataListener {
        fun onUpdate(message: Message)
        fun onDestroy()
    }
}