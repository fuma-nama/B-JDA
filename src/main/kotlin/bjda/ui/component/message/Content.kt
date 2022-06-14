package bjda.ui.component.message

import bjda.ui.core.Children
import bjda.ui.core.Component
import bjda.ui.core.RenderData

class Content(content: String) : Component<String, Nothing?>(content, null) {
    override fun onBuild(data: RenderData) {
        data.setContent(props)
    }

    override fun render(): Children? {
        return null
    }
}