package net.sonmoosans.bjdui.component.row

import net.sonmoosans.bjdui.component.action.Action
import net.sonmoosans.bjdui.component.action.toAction
import net.sonmoosans.bjda.ui.core.CProps
import net.sonmoosans.bjda.ui.core.ElementImpl
import net.sonmoosans.bjdui.core.internal.MessageBuilder
import net.sonmoosans.bjda.utils.LambdaBuilder
import net.sonmoosans.bjda.utils.LambdaList
import net.sonmoosans.bjda.utils.addActionRow
import net.sonmoosans.bjda.utils.build
import net.dv8tion.jda.api.interactions.components.ActionComponent
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

    constructor() : this(emptyList())
    constructor(vararg action: Action) : this(action.toList())
    constructor(vararg action: ActionComponent) : this(action.map { it.toAction() })

    private val rowSpace = 1.0

    override fun build(data: MessageBuilder) {
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

    companion object {
        fun LambdaBuilder<in RowLayout>.rowLayout(actions: List<Action>) = + RowLayout(actions)
        fun LambdaBuilder<in RowLayout>.rowLayout(actions: LambdaList<Action>) = + RowLayout(actions.build())

        operator fun rangeTo(actions: LambdaList<Action>): RowLayout {
            return RowLayout(actions.build())
        }
    }
}