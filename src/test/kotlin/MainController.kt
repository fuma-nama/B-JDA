import bjda.plugins.slashcommand.annotations.Command
import bjda.plugins.slashcommand.annotations.CommandGroup
import bjda.plugins.slashcommand.annotations.Event
import bjda.ui.component.*
import bjda.ui.core.*
import bjda.ui.core.Component.Companion.minus
import bjda.ui.core.Component.Companion.rangeTo
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@CommandGroup(name = "todo", description = "TODO List")
class MainController {
    @Command(name = "create", description = "Create TODO List")
    fun create(
        @Event event: SlashCommandInteractionEvent,
    ) {

        val start = System.currentTimeMillis()
        UI(
            TodoApp()
        ).reply(event)

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
}