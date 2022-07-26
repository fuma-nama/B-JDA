package bjda.ui.core

import bjda.ui.types.AnyElement
import net.dv8tion.jda.api.entities.Message

/**
 * UI instance that used for rendering only once
 *
 * Build and unmount components when init
 *
 * Notice that it will throw an exception if you add any hooks or used ui property in components
 */
open class UIOnce(element: ElementImpl<*>) {
    private val rendered: Message

    init {
        element.mount(null)
        renderElement(element)

        val data = RenderData()
        element.buildAll(data)

        rendered = data.build()

        element.unmount()
    }

    fun get(): Message {
        return rendered
    }

    fun renderElement(comp: AnyElement) {
        val rendered = comp.render()

        if (rendered != null) {

            for (child in rendered) {
                if (child != null) {
                    child.mount(comp)

                    renderElement(child)
                }
            }
        }

        comp.snapshot = rendered
    }
}