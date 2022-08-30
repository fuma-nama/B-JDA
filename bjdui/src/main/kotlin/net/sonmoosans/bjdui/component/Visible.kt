package net.sonmoosans.bjdui.component

import net.sonmoosans.bjdui.types.Children
import net.sonmoosans.bjdui.types.ComponentTree
import net.sonmoosans.bjdui.utils.AncestorFactory
import net.sonmoosans.bjda.utils.LambdaBuilder
import net.sonmoosans.bjda.ui.core.*

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

        fun LambdaBuilder<in Visible>.visible(visible: Boolean, comp: Children) = + Visible(visible) -comp
    }
}