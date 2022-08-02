package bjda.utils

import bjda.ui.component.Embed.Companion.toComponent
import bjda.ui.component.utils.Builder
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.MessageEmbed.*
import java.awt.Color
import java.time.OffsetDateTime

fun embed(
    url: String? = null,
    title: String? = null,
    description: String? = null,
    type: EmbedType? = null,
    timestamp: OffsetDateTime? = null,
    color: Color = Color.BLACK,
    thumbnail: Thumbnail? = null,
    provider: Provider? = null,
    author: AuthorInfo? = null,
    videoInfo: VideoInfo? = null,
    footer: Footer? = null,
    image: ImageInfo? = null,
    fields: List<Field>? = null,
): MessageEmbedImpl {
    return MessageEmbedImpl(url, title, description, type, timestamp, color.rgb, thumbnail, provider, author, videoInfo, footer, image, fields)
}

fun image(url: String? = null, proxy: String? = null, width: Int = 0, height: Int = 0): ImageInfo {
    return ImageInfo(url, proxy, width, height)
}

fun video(url: String? = null, width: Int = 0, height: Int = 0): VideoInfo {
    return VideoInfo(url, width, height)
}

fun provider(name: String? = null, url: String? = null): Provider {
    return Provider(name, url)
}

fun thumbnail(url: String? = null, proxyUrl: String? = null, width: Int = 0, height: Int = 0): Thumbnail {
    return Thumbnail(url, proxyUrl, width, height)
}

fun author(
    name: String? = null, url: String? = null,
    icon: String? = null, iconProxy: String? = null,
): AuthorInfo {
    return AuthorInfo(name, url, icon, iconProxy)
}

fun blank(inline: Boolean = false): Field {
    return Field("\u200e", "\u200e", inline)
}

fun field(name: String, value: String, inline: Boolean = false): Field {
    return Field(name, value, inline)
}

fun footer(
    text: String? = null,
    icon: String? = null,
    iconProxy: String? = null,
): Footer {
    return Footer(text, icon, iconProxy)
}

class MessageEmbedImpl(
    url: String?,
    title: String?,
    description: String?,
    type: EmbedType?,
    timestamp: OffsetDateTime?,
    color: Int,
    thumbnail: Thumbnail?,
    provider: Provider?,
    author: AuthorInfo?,
    videoInfo: VideoInfo?,
    footer: Footer?,
    image: ImageInfo?,
    fields: List<Field?>?,
) : MessageEmbed(url,
    title,
    description,
    type?: EmbedType.RICH,
    timestamp,
    color,
    thumbnail,
    provider,
    author,
    videoInfo,
    footer,
    image,
    fields),
    Convert<Builder> {
    override fun convert(): Builder {
        return toComponent()
    }
}