package bjda.ui.core

import bjda.ui.core.internal.RenderData
import bjda.ui.core.internal.Renderer.Companion.renderSingle
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
        renderSingle(element)

        val data = RenderData()
        element.buildAll(data)

        rendered = data.build()

        element.unmount()
    }

    fun get(): Message {
        return rendered
    }

    companion object {
        fun Element<*>.buildMessage(): Message {
            mount(null)
            renderSingle(this)

            val data = RenderData()
            buildAll(data)
            unmount()

            return data.build()
        }
    }
}