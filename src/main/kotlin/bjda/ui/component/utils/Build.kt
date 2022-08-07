package bjda.ui.component.utils

import bjda.ui.core.Component
import bjda.ui.core.IProps
import bjda.ui.core.internal.RenderData

class Builder(val builder: (RenderData) -> Unit) : Component<IProps>(IProps()) {
    override fun build(data: RenderData) {
        builder(data)
    }
}

fun build(builder: (RenderData) -> Unit): Builder {
    return Builder(builder)
}