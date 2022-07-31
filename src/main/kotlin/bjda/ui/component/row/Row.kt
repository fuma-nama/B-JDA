package bjda.ui.component.row

import bjda.ui.component.action.Action
import bjda.ui.component.action.toAction
import bjda.ui.core.CProps
import bjda.ui.core.ElementImpl
import bjda.ui.core.RenderData
import bjda.utils.LambdaList
import bjda.utils.build
import net.dv8tion.jda.api.interactions.components.ActionComponent
import net.dv8tion.jda.api.interactions.components.ActionRow

class Row(action: List<Action>) : ElementImpl<Row.Props>(Props(action)) {
    constructor(vararg action: Action) : this(
        action.toList()
    )

    constructor(action: LambdaList<Action>) : this(
        action.build()
    )

    constructor(vararg action: ActionComponent) : this(
        action.map { it.toAction() }
    )

    class Props(actions: List<Action>) : CProps<ActionRow>() {
        override var children = ActionRow.of(
            actions.map {
                it.build()
            }
        )
    }

    override fun build(data: RenderData) {
        data.addActionRow(build())
    }

    fun build(): ActionRow {
        return props.children
    }

    companion object {
        fun row(action: LambdaList<ActionComponent>): ActionRow {
            return ActionRow.of(action.build())
        }
    }
}