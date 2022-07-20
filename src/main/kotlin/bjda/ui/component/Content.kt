package bjda.ui.component

import bjda.ui.core.ElementImpl
import bjda.ui.core.IProps
import bjda.ui.core.RenderData

class Content(content: String) : ElementImpl<Content.Props>(Props(content)) {
    data class Props(val content: String): IProps()

    override fun build(data: RenderData) {
        data.setContent(props.content)
    }
}