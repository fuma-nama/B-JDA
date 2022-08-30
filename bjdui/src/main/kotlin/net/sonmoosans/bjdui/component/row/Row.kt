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

class Row : ElementImpl<Row.Props> {
    constructor(row: ActionRow) : super(Props(row))
    constructor(action: List<Action>) : super(Props(action))
    constructor(action: LambdaList<Action>) : this(action.build())

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

    override fun build(data: MessageBuilder) {

        data.addActionRow(build())
    }

    fun build(): ActionRow {
        return props.children
    }

    companion object {
        fun LambdaBuilder<in Row>.row(action: LambdaList<Action>) = + Row(action)

        fun LambdaBuilder<in Row>.row(vararg action: ActionComponent) = + Row(*action)

        fun LambdaBuilder<in Row>.rowOf(action: LambdaList<ActionComponent>) = + Row(
            ActionRow.of(action.build())
        )
    }
}