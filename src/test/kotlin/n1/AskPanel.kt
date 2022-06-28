package n1

import bjda.plugins.ui.hook.ButtonClick
import bjda.plugins.ui.modal.Form.Companion.form
import bjda.ui.component.Embed
import bjda.ui.component.Row
import bjda.ui.component.action.Button
import bjda.ui.component.action.TextField
import bjda.ui.core.Component
import bjda.ui.core.IProps
import bjda.ui.types.Children
import java.awt.Color

class AskPanel : Component<AskPanel.Props, AskPanel.State>(Props()) {
    class Props : IProps() {
        lateinit var onAsk: (Question) -> Unit
    }

    class State {
        var error: String? = null
    }

    override var state = State()

    private val onWrite = ButtonClick {event ->
        event.replyModal(questionForm).queue()
    }

    private val onSkip = ButtonClick {event ->

    }

    private val questionForm by form {
        title = "Write Question"

        val title = TextField("question") {
            label = "Question"
        }
        val option1 = TextField("option_1") {
            label = "Choice 1"
        }
        val option2 = TextField("option_2") {
            label = "Choice 2"
        }

        onSubmit = {event ->
            val answers = event.value(option1) to event.value(option2)

            if (answers.first == answers.second) {
                updateState {
                    error = "Choices cannot be duplicated"
                }

                ui.edit(event)
            } else {
                event.deferEdit().queue()

                val question = Question(event.value(title), answers)

                props.onAsk(question)
            }
        }

        rows = {
            + row(title)
            + row(option1)
            + row(option2)
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
                description = "You may skip your turn"
                color = Color.LIGHT_GRAY
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