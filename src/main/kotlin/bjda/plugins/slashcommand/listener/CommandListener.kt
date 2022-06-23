package bjda.plugins.slashcommand.listener

import bjda.plugins.slashcommand.CompiledRoutes
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class CommandListener(val compiled: CompiledRoutes) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val handler = compiled[event.commandId]
        handler?.handle(event)
    }
}