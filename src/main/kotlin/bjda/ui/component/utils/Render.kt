package bjda.ui.component.utils

import bjda.ui.core.ElementImpl
import bjda.ui.core.IProps
import bjda.ui.types.ComponentTree
import bjda.utils.LambdaBuilder

class Render(render: () -> Unit) : ElementImpl<Render.Props>(Props(render)) {
    class Props(val render: () -> Unit) : IProps()

    override fun render(): ComponentTree? {
        props.render()
        return null
    }
}

fun LambdaBuilder<in Render>.render(render: () -> Unit) =+ Render(render)