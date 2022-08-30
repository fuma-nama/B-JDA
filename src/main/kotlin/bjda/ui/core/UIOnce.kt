package bjda.ui.core

import bjda.ui.core.internal.Renderer.Companion.renderSingle
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder
import net.dv8tion.jda.api.utils.messages.MessageEditData


fun Element<*>.buildMessage(): MessageCreateData {
    mount(null, null)
    renderSingle(this)

    val data = MessageCreateBuilder()
    buildAll(data)
    unmount()

    return data.build()
}

fun Element<*>.buildEditMessage(): MessageEditData {
    mount(null, null)
    renderSingle(this)

    val data = MessageEditBuilder()
    buildAll(data)
    unmount()

    return data.build()
}