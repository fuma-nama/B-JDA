package bjda.ui.component

import bjda.ui.core.Component
import bjda.ui.core.IProps
import bjda.ui.types.Children

class Group(children: Children) : Component<Group.Props>(Props(children)) {
    data class Props(val children: Children): IProps()

    override fun onRender(): Children {
        return props.children
    }
}