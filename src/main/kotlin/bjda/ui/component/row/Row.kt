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

class Row : ElementImpl<Row.Props> {
    constructor(row: ActionRow) : super(Props(row))
    constructor(action: List<Action>) : super(Props(action))

    constructor(action: LambdaList<Action>) : this(
        action.build()
    )

    constructor(vararg action: Action) : this(
        action.toList()
    )

    constructor(vararg action: ActionComponent) : this(
        action.map { it.toAction() }
    )

    class Props(override var children: ActionRow) : CProps<ActionRow>() {

        constructor(actions: List<Action>) : this(
            ActionRow.of(
                actions.map {
                    it.build()
                }
            )
        )
    }

    override fun build(data: RenderData) {
        data.addActionRow(build())
    }

    fun build(): ActionRow {
        return props.children
    }

    companion object {
        fun ActionRow(action: LambdaList<ActionComponent>): ActionRow {
            return ActionRow.of(action.build())
        }

        fun rowOf(action: LambdaList<ActionComponent>): Row {
            return Row(
                ActionRow(action)
            )
        }
    }
}