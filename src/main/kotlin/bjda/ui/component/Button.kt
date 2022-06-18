package bjda.ui.component

import bjda.ui.core.Component
import bjda.ui.core.FProps
import bjda.ui.core.RenderData
import bjda.ui.types.Init
import net.dv8tion.jda.api.interactions.components.ActionRow

class Button(props: Init<Props>) : Component.NoState<Button.Props>(Props(), props) {
    data class Props(val text: String? = null) : FProps()

    override fun onBuild(data: RenderData) {
        data.addActionRow(ActionRow.of())
    }
}