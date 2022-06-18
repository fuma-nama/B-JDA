package bjda.ui.component

import bjda.ui.core.Component
import bjda.ui.core.FProps
import bjda.ui.core.RenderData
import bjda.utils.LambdaList

interface Action {

}

class Row(children: LambdaList<Action>) : Component.NoState<Row.Props>(Props(children)) {
    class Props(children: LambdaList<Action>) : FProps() {
        var actions: List<Action> = init(children)
    }

    override fun onBuild(data: RenderData) {
    }
}