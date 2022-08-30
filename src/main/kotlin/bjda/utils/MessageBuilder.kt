package bjda.utils

import bjda.ui.component.TextStyle
import bjda.ui.core.internal.MessageBuilder
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder
import net.dv8tion.jda.internal.requests.restaction.interactions.MessageEditCallbackActionImpl
import net.dv8tion.jda.internal.requests.restaction.interactions.ReplyCallbackActionImpl
import java.awt.Color
import java.time.OffsetDateTime

fun message(init: MessageCreateBuilder.() -> Unit): MessageCreateBuilder {
    return MessageCreateBuilder().apply(init)
}

fun IReplyCallback.reply(init: MessageCreateBuilder.() -> Unit): ReplyCallbackActionImpl {
    val action = this.deferReply() as ReplyCallbackActionImpl

    action.applyData(
        MessageCreateBuilder().apply(init).build()
    )

    return action
}

fun IMessageEditCallback.edit(init: MessageEditBuilder.() -> Unit): MessageEditCallbackActionImpl {
    val action = deferEdit() as MessageEditCallbackActionImpl

    action.applyData(
        MessageEditBuilder().apply(init).build()
    )

    return action
}

var MessageBuilder.text
    get() = this.content
    set(s) { this.setContent(s) }

fun MessageBuilder.text(
    content: String? = null,
    language: String? = null,
    type: TextStyle = TextStyle.Normal
) {
    text += when (type) {
        TextStyle.Normal -> content
        TextStyle.Line -> (content + "\n")
        TextStyle.CodeLine -> "`$content`"
        TextStyle.CodeBlock -> """
                    ```$language
                    $content
                    ```
                """.trimIndent()
    }
}

fun MessageBuilder.addActionRow(row: ActionRow) = setComponents(
    ArrayList(components).apply {
        add(row)
    }
)

fun MessageBuilder.embed(
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
    val embed = MessageEmbed(
        url, title, description, type, timestamp, color.rgb, thumbnail, provider, author, videoInfo, footer, image, fields
    )

    setEmbeds(embeds + embed)
}
