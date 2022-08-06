package bjda.ui.component

import bjda.ui.core.*
import bjda.ui.types.Children
import bjda.ui.types.ComponentTree
import bjda.ui.utils.AncestorFactory
import bjda.utils.LambdaBuilder

/**
 * Hide or Show A Element
 */
class Visible : ElementImpl<Visible.Props> {
    constructor() : super(Props())
    constructor(visible: Boolean) : super(Props(visible))

    class Props() : CProps<Children>() {
        var visible: Boolean = false

        constructor(visible: Boolean) : this() {
            this.visible = visible
        }
    }

    override fun render(): ComponentTree? {
        return if (props.visible)
            parseChildren(props.children)
        else null
    }

    companion object : AncestorFactory<Visible, Props> {
        override fun create(init: Props.() -> Children): Visible {
            return Visible()..init
        }

        fun LambdaBuilder<in Visible>.visible(visible: Boolean, comp: Children) = + Visible(visible)-comp
    }
}