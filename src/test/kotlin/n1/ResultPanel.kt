package n1

import bjda.plugins.ui.hook.ButtonClick
import bjda.ui.component.Embed
import bjda.ui.component.Row
import bjda.ui.component.action.Button
import bjda.ui.core.Component.Companion.minus
import bjda.ui.core.Component.Companion.rangeTo
import bjda.ui.core.FComponent
import bjda.ui.core.IProps
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import java.awt.Color

class ResultPanelProps : IProps() {
    lateinit var answer: Answer
    lateinit var onConfirm: () -> Unit
    var score: Int = 0
    var isCorrect = false
}
val ResultPanel = FComponent.noState(::ResultPanelProps) {
    val onConfirm = ButtonClick {event ->
        ui.switchTo(WaitingPlayersPanel(), false)

        ui.edit(event) {
            props.onConfirm()
        }
    };

    {
        + Embed()..{
            with (props) {
                title = "The Answer is ${answer.name} with ${answer.votes} Votes"
                description = "Now you have ${props.score} Scores"

                color = if (isCorrect) Color.GREEN else Color.RED
            }
        }

        + Row() -{
            + Button(id = use(onConfirm)) {
                label = "Confirm"
                style = ButtonStyle.SUCCESS
            }
        }
    }
}