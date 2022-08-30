package bjda.ui.component

import bjda.ui.core.ElementImpl
import bjda.ui.core.IProps
import bjda.ui.core.internal.MessageBuilder
import bjda.utils.LambdaBuilder

class Content(content: String) : ElementImpl<Content.Props>(Props(content)) {
    data class Props(val content: String): IProps()

    override fun build(data: MessageBuilder) {
        data.setContent(props.content)
    }

    companion object {
        fun LambdaBuilder<in Content>.content(content: String) = + Content(content)
    }
}