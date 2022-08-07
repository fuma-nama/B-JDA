package bjda.ui.component.utils

import bjda.ui.core.ElementImpl
import bjda.ui.core.IProps
import bjda.ui.core.internal.RenderData
import bjda.utils.LambdaBuilder

/**
 * Call the listener when building the component
 */
class Builder(builder: (RenderData) -> Unit) : ElementImpl<Builder.Props>(Props(builder)) {
    class Props(val builder: (RenderData) -> Unit) : IProps()

    override fun build(data: RenderData) = props.builder(data)
}

fun LambdaBuilder<in Builder>.build(builder: (RenderData) -> Unit) =+ Builder(builder)