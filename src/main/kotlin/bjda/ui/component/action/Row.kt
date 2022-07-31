package bjda.ui.component.action

import bjda.ui.component.utils.Builder
import bjda.ui.core.CProps
import bjda.ui.core.ElementImpl
import bjda.ui.core.RenderData
import bjda.utils.LambdaList
import bjda.utils.build
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ItemComponent

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

fun row(vararg actions: ItemComponent): Builder {
    return Builder {
        it.addActionRow(
            ActionRow.of(*actions)
        )
    }
}