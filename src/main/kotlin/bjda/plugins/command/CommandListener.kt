package bjda.plugins.command

import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.output.CliktConsole
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color
//TODO: support mention args and more types

abstract class CommandListener(val commands: Array<out CliktCommand>): ListenerAdapter() {
    abstract var prefix: String

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        var input = event.message.contentRaw

        if (input.startsWith(prefix)) {
            input = input.removePrefix(prefix)

            val error = RootCommand(event).execute(input)
            println(input)

            if (error != null) {

                event.message.replyEmbeds(
                    EmbedBuilder()
                        .setTitle("Usage Error")
                        .setDescription(error)
                        .setColor(Color.RED)
                        .build()
                ).queue()
            }
        }
    }

    inner class RootCommand(private val event: MessageReceivedEvent) : CliktCommand(), CliktConsole {
        override val lineSeparator: String = "\n"

        init {
            context {
                console = this@RootCommand
            }
        }

        fun execute(input: String): String? {
            try {
                subcommands(*commands).parse(input.split(" "))

            } catch (e: UsageError) {
                return e.text
            } catch (e: Abort) {
                return e.message
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        override fun run() {
            currentContext.obj = event
        }

        override fun print(text: String, error: Boolean) {
            if (error) {
                reply(
                    EmbedBuilder()
                        .setTitle("Error")
                        .setDescription(text)
                        .setColor(Color.RED)
                        .build()
                )
            } else {
                reply(text)
            }
        }

        override fun promptForLine(prompt: String, hideInput: Boolean): String? {
            throw UsageError("Prompt is not supported on bjda as it requires async")
        }

        private fun reply(text: String) {
            event.message.reply(text).queue()
        }

        private fun reply(embed: MessageEmbed) {
            event.message.replyEmbeds(embed).queue()
        }
    }
}