package bjda.ui.component

import bjda.ui.core.CProps
import bjda.ui.core.ElementImpl
import bjda.ui.core.parseChildren
import bjda.ui.core.rangeTo
import bjda.ui.types.AnyElement
import bjda.ui.types.Children
import bjda.ui.types.ComponentTree
import bjda.ui.utils.ElementFactory
import bjda.utils.LambdaBuilder

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