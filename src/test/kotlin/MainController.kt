import bjda.plugins.command.annotations.Command
import bjda.plugins.command.annotations.CommandGroup
import bjda.plugins.command.annotations.Event
import bjda.plugins.ui.AutoReply
import bjda.plugins.ui.hook.ButtonClick
import bjda.plugins.ui.modal.Form
import bjda.plugins.ui.modal.Input
import bjda.ui.component.Row
import bjda.ui.component.Text
import bjda.ui.component.TextType
import bjda.ui.component.action.Button
import bjda.ui.component.action.TextField
import bjda.ui.core.*
import bjda.ui.core.Component
import bjda.ui.core.hooks.Context
import bjda.ui.types.Children
import bjda.utils.build
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent


val Data = Context.create<String>()

@CommandGroup(name = "todo", description = "TODO List")
class MainController {
    @Command(name = "create", description = "Create TODO List")
    fun create(
        @Event event: SlashCommandInteractionEvent,
    ) {

        val start = System.currentTimeMillis()

        val manager = ComponentManager(
            TodoApp()
        )

        event.reply(manager.build()).queue()

        val end = System.currentTimeMillis()
        println("Took: ${end - start} ms")
    }

    class TodoApp : Component<IProps, TodoApp.State>(IProps()) {
        data class State(var content: List<String> = ArrayList())

        init {
            this.state = State()
        }

        private val addToDoForm = Form {
            title = "Add Todo"

            onSubmit = {event ->
                addToDo(event.getValue("todo")!!.asString)
                manager.edit(event)
            }

            rows = {
                + Input {
                    + TextField("todo") {
                        label = "TODO"
                    }
                }
            }
        }

        private val onClick = ButtonClick { event ->
            event.replyModal(addToDoForm.create()).queue()

            AutoReply.OFF
        }

        private fun addToDo(content: String = "Empty") {
            updateState {
                state.content += content
            }
        }

        override fun onRender(): Children {
            use(Data.Provider("Update State ${state.content.size}"))

            return {
                + Text()..{
                    content = "**TODO List**"
                    type = TextType.LINE
                }

                + if (state.content.isEmpty()) Text()..{
                    content = "Empty"
                    type = TextType.CODE_BLOCK
                } else null

                + state.content.map {
                    TodoItem()-it
                }

                + Row()-{
                    + Button {
                        id = use(onClick)
                        label = "Add"
                    }
                }
            }
        }

        private class TodoItem : NoState<TodoItem.Props>(Props()) {
            class Props : CProps<String>()

            override fun onRender(): Children {
                return {
                    + Text()..{
                        content = props.children
                        type = TextType.CODE_BLOCK
                    }
                }
            }
        }

    }
}