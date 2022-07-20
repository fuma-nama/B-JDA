package bjda.ui.component

import bjda.ui.core.ElementImpl
import bjda.ui.core.IProps
import bjda.ui.types.AnyElement
import bjda.ui.types.ComponentTree

class Fragment(components: ComponentTree) : ElementImpl<Fragment.Props>(Props(components)) {
    constructor(components: Collection<AnyElement?>) : this(components.toTypedArray())

    class Props(val components: ComponentTree) : IProps()

    override fun render(): ComponentTree {
        return props.components
    }
}