package test

import bjda.plugins.command.annotations.Command
import bjda.plugins.command.annotations.CommandGroup
import bjda.plugins.command.annotations.Event
import bjda.plugins.command.annotations.Param
import bjda.plugins.command.annotations.optional.ChannelOption
import bjda.plugins.command.annotations.optional.Choices
import bjda.plugins.command.annotations.optional.Choices.Choice
import bjda.plugins.command.annotations.optional.Range
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@CommandGroup(name = "test", description = "Testing Commands")
class MainController {
    @CommandGroup(name = "say", description = "Say something")
    class Say {
        @Command(name = "hello", description = "Say Hello")
        fun hello(
            @Event event: SlashCommandInteractionEvent,

            @Param("name", "User Name")
            @Choices(
                Choice("big", "You")
            ) userName: String,

            @Param("count", "Say Count", required = false)
            @Range(from = "1", to = "100") count: Long,
        ) {

            event.reply()
            println(count)
            println(event.commandId)

            event.reply(userName).queue()
        }
    }

    @CommandGroup(name = "kill", description = "Kill people")
    class Kill {
        @Command(name = "hello", description = "Say Hello")
        fun kill() {

        }
    }
}