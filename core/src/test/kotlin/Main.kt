package todo

import net.sonmoosans.bjda.bjda
import net.sonmoosans.bjda.wrapper.Mode
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.sonmoosans.bjda.plugins.supercommand.builder.choice
import net.sonmoosans.bjda.plugins.supercommand.builder.command
import net.sonmoosans.bjda.plugins.supercommand.builder.messageCommand
import net.sonmoosans.bjda.plugins.supercommand.builder.userCommand
import net.sonmoosans.bjda.plugins.supercommand.supercommand

suspend fun main() {
    bjda(Mode.Light) {
        config {
            setToken(System.getenv("TOKEN"))
        }

        supercommand(
            TestCommand,
            MessageHelloCommand,
            UserHelloCommand,
        )
    }
}

val TestCommand = command(name = "test", description = "Example Command") {
    val size = int("size", "Size of Text")
        .optional()
        .map({"${it}px"}) {
            choices {
                choice("sm", 1)
                choice("md", 2)
                choice("lg", 5)
            }

            default { 0 }
        }

    execute {
        event.reply(size.value).queue()
    }
}

val UserHelloCommand = userCommand(name = "hello") {
    execute { event ->
        event.reply("Hello").queue()
    }
}

val MessageHelloCommand = messageCommand(name = "hello") {
    name(DiscordLocale.CHINESE_TAIWAN, "測試命令")

    execute { event ->
        event.reply("Hello").queue()
    }
}