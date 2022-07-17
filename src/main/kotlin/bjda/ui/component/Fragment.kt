package bjda.ui.component

import bjda.ui.core.Component
import bjda.ui.core.IProps
import bjda.ui.types.AnyComponent
import bjda.ui.types.Children

class Fragment(components: Collection<AnyComponent?>) : Component<Fragment.Props>(Props(components)) {
    data class Props(val components: Collection<AnyComponent?>) : IProps()

    override fun onRender(): Children {
        return {
            + props.components
        }
    }
}