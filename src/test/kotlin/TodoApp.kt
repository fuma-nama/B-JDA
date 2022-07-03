import bjda.plugins.ui.hook.ButtonClick
import bjda.plugins.ui.hook.MenuSelect
import bjda.plugins.ui.modal.Form.Companion.form
import bjda.ui.component.RowLayout
import bjda.ui.component.Text
import bjda.ui.component.TextType
import bjda.ui.component.action.Button
import bjda.ui.component.action.Menu
import bjda.ui.component.action.TextField
import bjda.ui.core.Component
import bjda.ui.core.IProps
import bjda.ui.types.Children
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle

class TodoApp : Component<IProps>(IProps()) {
    private val state = useCombinedState(State())

    data class State(
        val todos: ArrayList<String> = ArrayList(),
        var selected: Int? = null
    )

    private val onAddItem = ButtonClick { event ->
        event.replyModal(addTodoForm).queue()
    }

    private val onEditItem = ButtonClick { event ->
        event.replyModal(editTodoForm).queue()
    }

    private val onDeleteItem = ButtonClick {
        state update {
            todos.removeAt(selected!!)

            selected = null
        }

        ui.edit(it)
    }

    private val onSelectItem = MenuSelect { event ->
        state update {
            selected = todos.indexOf(
                event.selectedOptions[0].value
            )
        }

        ui.edit(event)
    }

    override fun onRender(): Children {
        val (todos, selected) = state.get()

        return {
            + Text()..{
                content = "**TODO List**"
                type = TextType.LINE
            }

            + on (todos.isEmpty()) {
                Text()..{
                    content = "No Todos"
                    type = TextType.CODE_BLOCK
                }
            }

            + todos.map {
                todoItem(it)
            }

            + RowLayout() -{
                addIf (todos.isNotEmpty()) {
                    Menu(id = use(onSelectItem)) {
                        placeholder = "Select a Item"

                        options = todos.mapIndexed {i, todo ->
                            SelectOption.of(todo, todo).withDefault(i == selected)
                        }
                    }
                }

                + Button(id = use(onAddItem)) {
                    label = "Add"
                }

                + where (selected != null) {
                    + Button(id = use(onEditItem)) {
                        label = "Edit"
                        style = ButtonStyle.PRIMARY
                    }
                    + Button(id = use(onDeleteItem)) {
                        label = "Delete"
                        style = ButtonStyle.DANGER
                    }
                }
            }
        }
    }

    private fun todoItem(content: String): Text {
        return Text()..{
            this.content = content
            type = TextType.CODE_BLOCK
        }
    }

    private val addTodoForm by form {
        title = "Add Todo"

        onSubmit = {event ->
            state update {
                todos += event.getValue("todo")!!.asString
            }

            ui.edit(event)
        }

        render = {
            + row {
                + TextField("todo") {
                    label = "TODO"
                    style = TextInputStyle.PARAGRAPH
                }
            }
        }
    }

    private val editTodoForm by form {
        title = "Modify Todo"

        onSubmit = {event ->
            val value = event.getValue("todo")!!.asString

            state update {
                todos[selected!!] = value
            }

            ui.edit(event)
        }

        render = {
            val (todos, selected) = state.get()

            + row {
                + TextField("todo") {
                    label = "New Content"
                    value = todos[selected!!]
                    style = TextInputStyle.PARAGRAPH
                }
            }
        }
    }
}