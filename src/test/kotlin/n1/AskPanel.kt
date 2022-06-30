package n1

import bjda.plugins.ui.hook.ButtonClick
import bjda.plugins.ui.hook.MenuSelect
import bjda.plugins.ui.modal.Form.Companion.form
import bjda.ui.component.Embed
import bjda.ui.component.Row
import bjda.ui.component.action.Button
import bjda.ui.component.action.Menu
import bjda.ui.component.action.Menu.Companion.createOptions
import bjda.ui.component.action.TextField
import bjda.ui.core.Component
import bjda.ui.core.IProps
import bjda.ui.types.Children
import java.awt.Color

class AskPanel : Component<AskPanel.Props, AskPanel.State>(Props()) {
    class Props : IProps() {
        lateinit var onAsk: (Question) -> Unit
        lateinit var onSkip: () -> Unit
    }

    class State {
        var error: String? = null
        var optionCount: Int = 2
    }

    override var state = State()

    private val onWrite = ButtonClick {event ->
        event.replyModal(questionForm).queue()
    }

    private val onSkip = ButtonClick {event ->
        event.deferEdit().queue()
        props.onSkip()
    }

    private val onSelectOptionsCount = MenuSelect {event->
        updateState {
            optionCount = event.selectedOptions[0].value.toInt()
        }

        ui.edit(event)
    }

    private val questionForm by form {
        title = "Write Question"

        val title = TextField("question") {
            label = "Question"
        }

        val options = (1..state.optionCount).map {i ->
            TextField("$i") {
                label = "Choice $i"
            }
        }

        onSubmit = {event ->
            val choices = options.map {
                event.value(it.id)
            }

            if (choices.distinct().size != choices.size) {
                updateState {
                    error = "Choices cannot be duplicated"
                }

                ui.edit(event)
            } else {
                event.deferEdit().queue()

                val question = Question(event.value(title), choices)

                props.onAsk(question)
            }
        }

        rows = {
            + row(title)

            + options.map {
                row(it)
            }
        }
    }

    override fun onRender(): Children {
        return {
            + on(state.error != null) {
                Embed()..{
                    title = "Error"
                    description = state.error
                    color = Color.RED
                }
            }

            + Embed()..{
                title = "Write your Question"
                description = "You may skip your turn."

                color = Color.LIGHT_GRAY
            }

            + Embed()..{
                description = "Specify options amount as you wanted with the below select menu!"
            }

            + Row() -{
                + Menu(id = use(onSelectOptionsCount)) {
                    placeholder = "Select Options Count"

                    options = createOptions(
                        state.optionCount.toString(),
                        "2" to "2",
                        "4" to "4"
                    )
                }
            }

            + Row() -{
                + Button(id = use(onWrite)) {
                    label = "Write"
                }
                + Button(id = use(onSkip)) {
                    label = "Skip"
                }
            }
        }
    }
}