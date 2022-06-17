package bjda.ui.component

import bjda.ui.core.BasicComponent
import bjda.ui.core.RenderData
import net.dv8tion.jda.api.interactions.components.ActionRow


class Button(props: Props) : BasicComponent<Button.Props>(props, null) {
    data class Props(val text: String? = null)

    override fun onBuild(data: RenderData) {
        data.addActionRow(ActionRow.of())
    }
}