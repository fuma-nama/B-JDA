package net.sonmoosans.bjdui.component

import net.sonmoosans.bjda.ui.core.CProps
import net.sonmoosans.bjda.ui.core.ElementImpl
import net.sonmoosans.bjda.ui.core.parseChildren
import net.sonmoosans.bjda.ui.core.rangeTo
import net.sonmoosans.bjdui.types.AnyElement
import net.sonmoosans.bjdui.types.Children
import net.sonmoosans.bjdui.types.ComponentTree
import net.sonmoosans.bjdui.utils.ElementFactory
import net.sonmoosans.bjda.utils.LambdaBuilder

class Fragment : ElementImpl<Fragment.Props> {
    constructor() : super(Props())
    constructor(components: ComponentTree) : super(Props(components))
    constructor(components: Collection<AnyElement?>) : this(components.toTypedArray())

    class Props() : CProps<ComponentTree>() {

        constructor(components: ComponentTree) : this() {
            children = components
        }
    }

    override fun render(): ComponentTree {
        return props.children
    }

    companion object : ElementFactory<Fragment, Props, ComponentTree> {
        override fun create(init: Props.() -> ComponentTree) = Fragment()..init

        fun LambdaBuilder<in Fragment>.fragment(components: Collection<AnyElement?>) =
            + Fragment(components)

        fun LambdaBuilder<in Fragment>.fragment(children: Children) =
            + Fragment(parseChildren(children))
    }
}