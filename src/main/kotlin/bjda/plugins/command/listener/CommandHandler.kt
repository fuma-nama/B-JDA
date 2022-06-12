package bjda.plugins.command.listener

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface CommandHandler {
    fun handle(event: SlashCommandInteractionEvent)
}