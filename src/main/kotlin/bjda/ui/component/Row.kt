package bjda.ui.component

import bjda.ui.core.Component
import bjda.ui.core.IProps
import bjda.ui.core.RenderData
import bjda.utils.LambdaList
import bjda.utils.build

interface Action {

}

class Row(children: LambdaList<Action>) : Component.NoState<Row.Props>(Props(children)) {
    class Props(children: LambdaList<Action>) : IProps() {
        var actions: List<Action> = children.build()
    }
}