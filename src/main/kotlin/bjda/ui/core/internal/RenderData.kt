package bjda.ui.core.internal

import bjda.ui.core.apply
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Message.MentionType
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import net.dv8tion.jda.api.utils.AttachmentOption
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.internal.entities.DataMessage
import net.dv8tion.jda.internal.utils.Checks
import java.io.InputStream
import java.util.*

fun InteractionHook.edit(message: RenderedMessage) = editOriginal(message).apply {
    for (file in message.files) {
        addFile(file.data, file.name)
    }
}

fun IMessageEditCallback.edit(message: RenderedMessage) = editMessage(message).apply {
    for (file in message.files) {
        addFile(file.data, file.name)
    }
}
fun Message.replyRendered(message: RenderedMessage) = reply(message).apply {
    for (file in message.files) {
        addFile(file.data, file.name)
    }
}

fun IReplyCallback.replyRendered(message: RenderedMessage) = reply(message).apply {
    for (file in message.files) {
        addFile(file.data, file.name)
    }
}

fun Message.edit(message: RenderedMessage) = editMessage(message).apply {
    for (file in message.files) {
        addFile(file.data, file.name)
    }
}

open class RenderData : MessageBuilder() {
    val files = arrayListOf<FileUpload>()

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

    open fun addFile(
        data: InputStream,
        name: String,
        vararg options: AttachmentOption,
    ) {
        Checks.notNull(data, "Data")
        Checks.notEmpty(name, "Name")
        Checks.noneNull(options, "Options")

        val fileName = if (options.isNotEmpty())
            "SPOILER_$name"
        else
            name

        files.add(FileUpload.fromData(data, fileName))
    }

    override fun build(): RenderedMessage {
        val message = builder.toString()
        check(!this.isEmpty) {
            "Cannot build a Message with no content. (You never added any content to the message)"
        }
        check(message.length <= Message.MAX_CONTENT_LENGTH) {
            "Cannot build a Message with more than " + Message.MAX_CONTENT_LENGTH + " characters. Please limit your input."
        }

        return RenderedMessage(
            isTTS, message, nonce, embeds,
            allowedMentions, mentionedUsers.toTypedArray(), mentionedRoles.toTypedArray(),
            components.toTypedArray(),
            ArrayList(stickers),
            files
        )
    }
}

/**
 * Message rendered with RenderData, contains additional information
 */

class RenderedMessage(
    tts: Boolean, content: String?, nonce: String?, embeds: Collection<MessageEmbed>,
    allowedMentions: EnumSet<MentionType>?, mentionedUsers: Array<String>, mentionedRoles: Array<String>,
    components: Array<LayoutComponent>, stickers: Collection<StickerSnowflake>,
    val files: Collection<FileUpload>,
) : DataMessage(
    tts, content, nonce, embeds, allowedMentions, mentionedUsers, mentionedRoles, components, stickers
)