package bjda.utils

import bjda.ui.component.TextStyle
import bjda.ui.core.RenderData
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ItemComponent
import java.awt.Color
import java.time.OffsetDateTime

fun message(init: MessageBuilder.() -> Unit): MessageBuilder {
    return MessageBuilder().apply(init)
}

fun IReplyCallback.reply(init: MessageBuilder.() -> Unit) = this.reply(
    MessageBuilder().apply(init).build()
)

fun IMessageEditCallback.edit(init: MessageBuilder.() -> Unit) = editMessage(
    MessageBuilder().apply(init).build()
)

open class MessageBuilder: RenderData() {
    fun content(text: String) {
        setContent(text)
    }

    fun text(
        content: String? = null,
        language: String? = null,
        type: TextStyle = TextStyle.NORMAL
    ) {
        when (type) {
            TextStyle.NORMAL -> append(content)
            TextStyle.LINE -> appendLine(content)
            TextStyle.CODE_LINE -> appendCodeLine(content)
            TextStyle.CODE_BLOCK -> appendCodeBlock(content, language)
        }
    }

    fun embed(
        url: String? = null,
        title: String? = null,
        description: String? = null,
        type: EmbedType? = null,
        timestamp: OffsetDateTime? = null,
        color: Color = Color.BLACK,
        thumbnail: MessageEmbed.Thumbnail? = null,
        provider: MessageEmbed.Provider? = null,
        author: MessageEmbed.AuthorInfo? = null,
        videoInfo: MessageEmbed.VideoInfo? = null,
        footer: MessageEmbed.Footer? = null,
        image: MessageEmbed.ImageInfo? = null,
        fields: List<MessageEmbed.Field>? = null,
    ) {
        val embed = MessageEmbedImpl(
            url, title, description, type, timestamp, color.rgb, thumbnail, provider, author, videoInfo, footer, image, fields
        )

        addEmbeds(embed)
    }

    fun embeds(vararg embeds: MessageEmbed) {
        addEmbeds(* embeds)
    }

    fun embeds(embeds: Collection<MessageEmbed>) {
        addEmbeds(embeds)
    }

    fun row(components: Collection<ItemComponent>) {
        addActionRow(ActionRow.of(components))
    }

    fun row(vararg components: ItemComponent) {
        addActionRow(ActionRow.of(* components))
    }
    fun rows(rows: Collection<ActionRow>) {
        addActionRow(rows)
    }

    fun rows(vararg rows: ActionRow) {
        addActionRow(* rows)
    }
}