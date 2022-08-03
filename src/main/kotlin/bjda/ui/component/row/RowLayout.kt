package bjda.ui.component.row

import bjda.ui.component.action.Action
import bjda.ui.core.CProps
import bjda.ui.core.ElementImpl
import bjda.ui.core.RenderData
import bjda.utils.LambdaList
import bjda.utils.build
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ItemComponent
import java.util.Stack

/**
 * Improve version of Row component
 *
 * Auto create a row when no space available
 */
class RowLayout(actions: List<Action>) : ElementImpl<RowLayout.Props>(Props(actions)) {

    class Props(actions: List<Action>) : CProps<List<Action>>() {
        override var children = actions
    }

    constructor(vararg action: Action) : this(action.toList())
    constructor(action: LambdaList<Action>) : this(action.build())

    private val rowSpace = 1.0

    override fun build(data: RenderData) {
        val row: Stack<ItemComponent> = Stack()
        val actions = props.children
        var space = rowSpace

        actions.forEach {
            val item = it.build()
            val size = rowSpace / item.type.maxPerRow

            if (size <= space) {
                space -= size
            } else {
                data.addActionRow(ActionRow.of(row))

                row.clear()
                space = rowSpace
            }

            row.push(item)
        }

        if (row.isNotEmpty())
            data.addActionRow(ActionRow.of(row))
    }
}