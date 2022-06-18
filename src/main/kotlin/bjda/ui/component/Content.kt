package bjda.ui.component

import bjda.ui.core.Component
import bjda.ui.core.FProps
import bjda.ui.core.RenderData

class Content(props: Props) : Component.NoState<Content.Props>(props) {
    data class Props(val content: String): FProps()

    override fun onBuild(data: RenderData) {
        data.setContent(props.content)
    }
}