package bjda.ui.component

import bjda.ui.core.Component
import bjda.ui.core.IProps
import bjda.ui.core.RenderData
import bjda.ui.types.Init
import net.dv8tion.jda.api.interactions.components.ActionRow

class Button : Component.NoState<Button.Props>(Props()) {
    data class Props(val text: String? = null) : IProps()

    override fun build(data: RenderData) {
        data.addActionRow(ActionRow.of())
    }
}