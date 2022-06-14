package bjda.ui.core

import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow

class RenderData : MessageBuilder() {
    fun addActionRow(vararg rows: ActionRow) {
        this.addActionRow(listOf(*rows))
    }

    fun addActionRow(rows: Collection<ActionRow>)  {
        components.addAll(rows)
    }

    fun addEmbeds(vararg rows: MessageEmbed) {
        addEmbeds(listOf(*rows))
    }

    fun addEmbeds(rows: Collection<MessageEmbed>) {
        embeds.addAll(rows)
    }
}