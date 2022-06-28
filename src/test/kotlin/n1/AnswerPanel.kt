package n1

import bjda.plugins.ui.hook.MenuSelect
import bjda.ui.component.Embed
import bjda.ui.component.Row
import bjda.ui.component.action.Menu
import bjda.ui.core.Component.Companion.minus
import bjda.ui.core.Component.Companion.rangeTo
import bjda.ui.core.FComponent
import bjda.ui.core.IProps
import net.dv8tion.jda.api.interactions.components.selections.SelectOption

class AnswerPanelProps : IProps() {
    lateinit var question: Question
    lateinit var player: Player
    lateinit var onAnswer: (player: Player, chose: String) -> Unit
}
val AnswerPanel = FComponent.noState(::AnswerPanelProps) {
    val onSelect = MenuSelect {event ->
        ui.switchTo(WaitingPlayersPanel(), false)

        ui.edit(event) {
            with(props) {
                onAnswer(player, event.selectedOptions[0].value)
            }
        }
    };

    {
        val question = props.question

        + Embed()..{
            title = question.title
            description = "Answer the question with the select menu below"
        }
        + Row() -{
            + Menu(id = use(onSelect)) {
                placeholder = "Select a Answer"

                options = question.answers.toList().map {
                    SelectOption.of(it.name, it.name)
                }
            }
        }
    }
}