import bjda.plugins.command.annotations.Command
import bjda.plugins.command.annotations.CommandGroup
import bjda.plugins.command.annotations.Event
import bjda.ui.component.Embed
import bjda.ui.component.Text
import bjda.ui.component.TextType
import bjda.ui.core.*
import bjda.ui.core.Component
import bjda.ui.core.hooks.Context
import bjda.ui.types.Children
import bjda.ui.listener.InteractionUpdateHook
import bjda.utils.build
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import kotlin.collections.ArrayList

val Data = Context.create<String>()

@CommandGroup(name = "test", description = "Testing Commands")
class MainController {
    @CommandGroup(name = "say", description = "Say something")
    class Say {
        @Command(name = "hello", description = "Say Hello")
        fun hello(
            @Event event: SlashCommandInteractionEvent
        ) {

            val start = System.currentTimeMillis()

            val manager = ComponentManager(
                MyContainer()-{
                    + Text()..{
                        content = "Hello"
                        type = TextType.LINE
                    }
                }
            )

            event.reply(manager.build()).queue { hook ->
                manager.listen(InteractionUpdateHook(hook))
            }

            val end = System.currentTimeMillis()
            println("Took: ${end - start} ms")

        }
    }

    /**
     * Used to test performance, it is worse
     */
    class MyContainer : Component<MyContainer.Props, MyContainer.State>(Props()) {
        class Props : CProps<Children>()

        data class State(var content: List<String> = ArrayList())

        override fun onMount() {
            this.state = State()

            repeat(4) {
                addLmao()
            }
        }

        private fun addLmao() {
            println("Update State ${state.content.size}")

            updateState {
                content += "Lmao"
            }
        }

        override fun render(): Children {
            use(Data.Provider("Update State ${state.content.size}"))

            return {
                + props.children.build()
                + state.content.map {
                    MyComponent()..{ content = it }
                }
            }
        }

    }

    private class MyComponent : Component.NoState<MyComponent.Props>(Props()) {
        class Props : IProps() {
            lateinit var content: String
        }

        override fun onMount() {
            println("New $props")
        }

        override fun render(): Children {
            val data = use(Data)
            return {
                + Text()..{
                    content = data
                    type = TextType.LINE
                }
            }
        }
    }
}