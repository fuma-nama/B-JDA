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
    var answers: List<Answer>? = null
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

    val answers = props.answers

    val text = answers?.joinToString(" and ") {answer ->
        "\"${answer.name}\""
    };

    {
        + Embed()..{

            with (props) {
                title = if (answers != null) {
                    val are = if (answers.size == 1)
                        "is"
                    else
                        "are"

                    val votes = answers[0].votes

                    "The Answer $are $text with $votes Votes"
                } else {
                    "All answers has the same votes, No one is correct"
                }

                description = "Now you have $score Scores"

                if (isCorrect) {
                    color = Color.GREEN
                    footer = "Correct!"
                } else {
                    color = Color.RED
                    footer = "Wrong!"
                }
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