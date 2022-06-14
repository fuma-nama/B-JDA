package test

import bjda.plugins.command.annotations.Command
import bjda.plugins.command.annotations.CommandGroup
import bjda.plugins.command.annotations.Event
import bjda.ui.ComponentManager
import bjda.ui.component.message.Text
import bjda.ui.component.message.TextType
import bjda.ui.core.Children
import bjda.ui.core.Component
import bjda.ui.listener.HookListener
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.util.*

@CommandGroup(name = "test", description = "Testing Commands")
class MainController {
    @CommandGroup(name = "say", description = "Say something")
    class Say {
        @Command(name = "hello", description = "Say Hello")
        fun hello(
            @Event event: SlashCommandInteractionEvent
        ) {

            var comp: MyComponent

            val manager = ComponentManager(
                MyComponent().also { comp = it }
            )

            event.reply(manager.build()).queue { hook ->
                manager.listen(HookListener(hook))
            }

            Timer("SettingUp", false).schedule(
                object : TimerTask() {
                    override fun run() {
                        comp.updateState(State(
                            content = "Lmao"
                        ))
                    }

                }, 2000L)
        }
    }

    @CommandGroup(name = "kill", description = "Kill people")
    class Kill {
        @Command(name = "hello", description = "Say Hello")
        fun kill() {

        }
    }


    data class State(val content: String)
    class MyComponent : Component<Nothing?, State>(null, State("Default")) {
        override fun render(): Children {
            val (content) = state

            return child(
                ChildComponent("Hi"),
                if (content == "Lmao") ChildComponent(content) else null,
                ChildComponent(content + "bruh")
            )
        }
    }

    class ChildComponent(content: String) : Component<Nothing?, String>(null, content) {
        override fun render(): Children {

            return arrayOf(
                Text(Text.Props(state, TextType.LINE))
            )
        }
    }
}