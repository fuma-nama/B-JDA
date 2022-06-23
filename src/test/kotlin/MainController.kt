import bjda.plugins.slashcommand.annotations.Command
import bjda.plugins.slashcommand.annotations.CommandGroup
import bjda.plugins.slashcommand.annotations.Event
import bjda.plugins.ui.AutoReply
import bjda.plugins.ui.hook.ButtonClick
import bjda.plugins.ui.hook.MenuSelect
import bjda.plugins.ui.modal.Form
import bjda.plugins.ui.modal.Input
import bjda.ui.component.*
import bjda.ui.component.action.Button
import bjda.ui.component.action.Select
import bjda.ui.component.action.TextField
import bjda.ui.core.*
import bjda.ui.core.Component
import bjda.ui.core.Component.Companion.minus
import bjda.ui.core.Component.Companion.rangeTo
import bjda.ui.types.Children
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.interactions.components.selections.SelectOption


@CommandGroup(name = "todo", description = "TODO List")
class MainController {
    @Command(name = "create", description = "Create TODO List")
    fun create(
        @Event event: SlashCommandInteractionEvent,
    ) {

        val start = System.currentTimeMillis()

        val app = UI(
            TodoApp()
        )

        event.reply(app.build()).queue()

        val end = System.currentTimeMillis()
        println("Took: ${end - start} ms")
    }

    @Command(name = "settings", description = "Manage User Settings")
    fun settings(@Event event: SlashCommandInteractionEvent) {
        val app = UI(
            Pager()-{
                + TodoApp()
                + Text()..{
                    content = "Hello"
                }
                + Embed()..{
                    title = "Hello World"
                }
            }
        )

        app.reply(event)
    }

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
                use(addTodoForm)
            ).queue()

            AutoReply.OFF
        }

        private val onEditItem = ButtonClick { event ->
            event.replyModal(
                use(editTodoForm)
            ).queue()

            AutoReply.OFF
        }

        private val onDeleteItem = ButtonClick {
            updateState {
                todos.removeAt(selected!!)

                selected = null
            }

            AutoReply.EDIT
        }

        private val onSelectItem = MenuSelect { event ->
            updateState {
                selected = todos.indexOf(
                    event.selectedOptions[0].value
                )
            }

            AutoReply.EDIT
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

                + not (empty) {
                    Row()-{
                        + Select(use(onSelectItem)) {
                            placeholder = "Select a Item"

                            options = state.todos.mapIndexed {i, todo ->
                                SelectOption.of(todo, todo).withDefault(i == state.selected)
                            }
                        }
                    }
                }

                + Row()-{
                    + Button {
                        id = use(onAddItem)
                        label = "Add"
                    }
                    + where (state.selected != null) {
                        + Button {
                            id = use(onEditItem)
                            label = "Edit"
                            style = ButtonStyle.PRIMARY
                        }
                        + Button {
                            id = use(onDeleteItem)
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

                AutoReply.EDIT
            }

            rows = {
                + Input {
                    + TextField("todo") {
                        label = "TODO"
                    }
                }
            }
        }

        private val editTodoForm = Form {
            title = "Modify Todo"

            onSubmit = {event ->
                val value = event.getValue("todo")!!.asString

                updateState {
                    todos[selected!!] = value
                }

                AutoReply.EDIT
            }

            rows = {
                + Input {
                    + TextField("todo") {
                        label = "New Content"
                    }
                }
            }
        }
    }
}