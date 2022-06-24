import bjda.plugins.ui.hook.ButtonClick
import bjda.plugins.ui.hook.MenuSelect
import bjda.plugins.ui.modal.Form
import bjda.plugins.ui.modal.Input
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

class TodoApp : Component<IProps, TodoApp.State>(IProps()) {
    class State {
        var todos: ArrayList<String> = ArrayList()
        var selected: Int? = null
    }

    init {
        this.state = State()
    }

    private val onAddItem = ButtonClick { event ->
        event.replyModal(
            addTodoForm.create()
        ).queue()
    }

    private val onEditItem = ButtonClick { event ->
        event.replyModal(
            editTodoForm.create()
        ).queue()
    }

    private val onDeleteItem = ButtonClick {
        updateState {
            todos.removeAt(selected!!)

            selected = null
        }

        ui.edit(it)
    }

    private val onSelectItem = MenuSelect { event ->
        updateState {
            selected = todos.indexOf(
                event.selectedOptions[0].value
            )
        }

        ui.edit(event)
    }

    override fun onRender(): Children {
        val empty = state.todos.isEmpty()

        return {
            + Text()..{
                content = "**TODO List**"
                type = TextType.LINE
            }

            + on (empty) {
                Text()..{
                    content = "No Todos"
                    type = TextType.CODE_BLOCK
                }
            }

            + state.todos.map {
                todoItem(it)
            }

            + RowLayout() -{
                addIf (!empty) {
                    Menu(onSelectItem) {
                        placeholder = "Select a Item"

                        options = state.todos.mapIndexed {i, todo ->
                            SelectOption.of(todo, todo).withDefault(i == state.selected)
                        }
                    }
                }

                + Button(onAddItem) {
                    label = "Add"
                }

                + where (state.selected != null) {
                    + Button(onEditItem) {
                        label = "Edit"
                        style = ButtonStyle.PRIMARY
                    }
                    + onDeleteItem.button {
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

    private val addTodoForm = Form {
        title = "Add Todo"

        onSubmit = {event ->
            updateState {
                todos += event.getValue("todo")!!.asString
            }

            ui.edit(event)
        }

        rows = {
            + Input {
                + TextField("todo") {
                    label = "TODO"
                    style = TextInputStyle.PARAGRAPH
                }
            }
        }
    }

    private val editTodoForm = Form {
        title = "Modify Todo"

        with (state) {

            onSubmit = {event ->
                val value = event.getValue("todo")!!.asString

                updateState {
                    todos[selected!!] = value
                }

                ui.edit(event)
            }

            rows = {
                + Input {
                    + TextField("todo") {
                        label = "New Content"
                        value = todos[selected!!]
                        style = TextInputStyle.PARAGRAPH
                    }
                }
            }
        }

    }
}