package net.sonmoosans.bjda.plugins.command

import net.sonmoosans.bjda.utils.embed
import net.sonmoosans.bjda.utils.translateCommandline
import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.output.CliktConsole
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color

//TODO: support mention args and more types

abstract class CommandListener(val commands: Array<out TextCommand>): ListenerAdapter() {
    abstract var prefix: String

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        var input = event.message.contentRaw

        if (input.startsWith(prefix)) {
            input = input.removePrefix(prefix)

            RootCommand(event).execute(input)
        }
    }

    inner class RootCommand(private val event: MessageReceivedEvent) : CliktCommand(), CliktConsole {
        override val lineSeparator: String = "\n"

        init {
            context {
                console = this@RootCommand
            }
        }

        fun execute(input: String) {
            try {
                subcommands(*commands).parse(translateCommandline(input))

            } catch (e: UsageError) {
                error(e.text)
            } catch (e: Abort) {
                error(e.message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun run() {
            currentContext.obj = event
        }

        override fun print(text: String, error: Boolean) {
            if (error) {
                error(text)
            } else {
                reply(text)
            }
        }

        private fun error(message: String?) {
            event.message.replyEmbeds(
                embed(
                    title = "Error",
                    description = message,
                    color = Color.RED,
                )
            ).queue()
        }

        override fun promptForLine(prompt: String, hideInput: Boolean): String? {
            throw UsageError("Prompt is not supported on bjda as it requires async waiting")
        }

        private fun reply(text: String) {
            event.message.reply(text).queue()
        }
    }
}