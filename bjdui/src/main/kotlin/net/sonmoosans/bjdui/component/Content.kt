package net.sonmoosans.bjdui.component

import net.sonmoosans.bjda.ui.core.ElementImpl
import net.sonmoosans.bjda.ui.core.IProps
import net.sonmoosans.bjdui.core.internal.MessageBuilder
import net.sonmoosans.bjda.utils.LambdaBuilder

class Content(content: String) : ElementImpl<Content.Props>(Props(content)) {
    data class Props(val content: String): IProps()

    override fun build(data: MessageBuilder) {
        data.setContent(props.content)
    }

    companion object {
        fun LambdaBuilder<in Content>.content(content: String) = + Content(content)
    }
}