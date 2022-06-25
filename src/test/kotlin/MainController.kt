import bjda.plugins.supercommand.SuperCommand
import bjda.ui.component.*
import bjda.ui.core.*
import bjda.ui.core.Component.Companion.minus
import bjda.ui.core.Component.Companion.rangeTo

class CreateTodo: SuperCommand(group = "todo", name = "create", description = "Create a Todo List") {
    override fun run() {
        val start = System.currentTimeMillis()
        UI(
            TodoApp()
        ).reply(event)

        val end = System.currentTimeMillis()
        println("Took: ${end - start} ms")
    }
}

class TodoSettings: SuperCommand(group = "todo", name = "settings", description = "Manager Settings") {
    override fun run() {
        val app = UI(
            Pager()-{
                + TodoApp()
                + Text()..{
                    content = "Todo Settings"
                }
                + Embed()..{
                    title = "User Settings"
                }
            }
        )

        app.reply(event)
    }
}