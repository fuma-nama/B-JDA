import bjda.plugins.command.annotations.Command
import bjda.plugins.command.annotations.CommandGroup
import bjda.plugins.command.annotations.Event
import bjda.ui.component.Embed
import bjda.ui.component.Text
import bjda.ui.component.TextType
import bjda.ui.core.*
import bjda.ui.listener.InteractionUpdateHook
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

            val manager = ComponentManager(
                BadComponent()
            )

            event.reply(manager.build()).queue { hook ->
                manager.listen(InteractionUpdateHook(hook))
            }
        }
    }

    /**
     * Used to test performance, it is worse
     */
    class BadComponent : Component<Unit, BadComponent.State>(Unit) {
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

        override fun render(): LambdaChildren {
            val (content) = state

            return {
                + BadChildren("Hi")
                + content.map {
                    BadChildren(it)
                }
            }
        }

        private class BadChildren(content: String) : BasicComponent<String>(content) {
            override fun onMount(manager: ComponentManager) {
                super.onMount(manager)

                println("New $props")
            }

            override fun render(): LambdaChildren {
                return {
                    + Text(Text.Props(props, TextType.LINE))
                }
            }
        }
    }
}