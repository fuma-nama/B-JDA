package bjda.ui.component

import bjda.ui.core.BasicComponent
import bjda.ui.core.RenderData

class Content(content: String) : BasicComponent<String>(content) {
    override fun onBuild(data: RenderData) {
        data.setContent(props)
    }
}