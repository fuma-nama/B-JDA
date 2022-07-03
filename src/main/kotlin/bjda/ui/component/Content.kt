package bjda.ui.component

import bjda.ui.core.Component
import bjda.ui.core.IProps
import bjda.ui.core.RenderData

class Content(content: String) : Component<Content.Props>(Props(content)) {
    data class Props(val content: String): IProps()

    override fun onBuild(data: RenderData) {
        data.setContent(props.content)
    }
}