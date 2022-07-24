package bjda.ui.component

import bjda.ui.component.action.Action
import bjda.ui.core.CProps
import bjda.ui.core.ElementImpl
import bjda.ui.core.RenderData
import bjda.utils.LambdaList
import bjda.utils.build
import net.dv8tion.jda.api.interactions.components.ActionRow

class Row : ElementImpl<Row.Props>(Props()) {
    class Props : CProps<LambdaList<Action>>()

    override fun build(data: RenderData) {
        data.addActionRow(build())
    }

    fun build(): ActionRow {
        val actions = props.children.build()

        return ActionRow.of(
            actions.map {
                it.build()
            }
        )
    }
}