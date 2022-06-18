import bjda.plugins.command.annotations.Command
import bjda.plugins.command.annotations.CommandGroup
import bjda.plugins.command.annotations.Event
import bjda.ui.component.Text
import bjda.ui.component.TextType
import bjda.ui.core.Component
import bjda.ui.core.ComponentManager
import bjda.ui.core.FProps
import bjda.ui.types.Elements
import bjda.ui.listener.InteractionUpdateHook
import bjda.ui.types.Init
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import kotlin.collections.ArrayList

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
                BadComponent {}
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
    class BadComponent(props: Init<FProps>) : Component<FProps, BadComponent.State>(FProps(), props) {

        data class State(var content: List<String> = ArrayList())

        override fun onMount(manager: ComponentManager) {
            this.state = State()

            repeat(4) {
                addLmao()
            }
        }

        fun addLmao() {
            println("Update State ${state.content.size}")

            updateState {
                content += "Lmao"
            }
        }

        override fun render(): Elements {
            return {
                + BadChildren {
                    content = "fdsa"
                }
                + state.content.map {
                    BadChildren { content = it }
                }
            }
        }

        private class BadChildren(props: Init<Props>) : Component.NoState<BadChildren.Props>(Props(), props) {
            class Props : FProps() {
                lateinit var content: String
            }

            override fun onMount(manager: ComponentManager) {
                super.onMount(manager)

                println("New $props")
            }

            override fun render(): Elements {
                val text = props.content

                return {
                    + Text {
                        content = text
                        type = TextType.LINE
                    }
                }
            }
        }
    }
}