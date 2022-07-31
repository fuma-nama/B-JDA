package bjda.ui.component.action

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
class RowLayout : ElementImpl<RowLayout.Props>(Props()) {
    class Props : CProps<LambdaList<Action>>()
    private val rowSpace = 1.0

    override fun build(data: RenderData) {
        val actions = props.children.build()

        val row: Stack<ItemComponent> = Stack()
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