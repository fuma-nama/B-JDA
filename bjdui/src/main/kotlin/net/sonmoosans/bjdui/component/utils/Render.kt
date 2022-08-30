package net.sonmoosans.bjdui.component.utils

import net.sonmoosans.bjda.ui.core.ElementImpl
import net.sonmoosans.bjda.ui.core.IProps
import net.sonmoosans.bjdui.types.ComponentTree
import net.sonmoosans.bjda.utils.LambdaBuilder

class Render(render: () -> Unit) : ElementImpl<Render.Props>(Props(render)) {
    class Props(val render: () -> Unit) : IProps()

    override fun render(): ComponentTree? {
        props.render()
        return null
    }
}

fun LambdaBuilder<in Render>.render(render: () -> Unit) =+ Render(render)