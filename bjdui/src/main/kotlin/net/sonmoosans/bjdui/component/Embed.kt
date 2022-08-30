package net.sonmoosans.bjdui.component

import net.sonmoosans.bjdui.component.utils.Builder
import net.sonmoosans.bjdui.core.internal.MessageBuilder
import net.sonmoosans.bjdui.utils.LeafFactory
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.MessageEmbed.*
import net.sonmoosans.bjda.ui.core.ElementImpl
import net.sonmoosans.bjda.ui.core.IProps
import net.sonmoosans.bjda.ui.core.rangeTo
import net.sonmoosans.bjda.utils.LambdaBuilder
import net.sonmoosans.bjda.utils.embed
import java.awt.Color
import java.time.OffsetDateTime

class Embed : ElementImpl<Embed.Props>(Props()) {

    class Props : IProps() {
        var url: String? = null
        var title: String? = null
        var description: String? = null
        var type: EmbedType? = null
        var timestamp: OffsetDateTime? = null
        var color: Color = Color.BLACK
        var thumbnail: Thumbnail? = null
        var provider: Provider? = null
        var author: AuthorInfo? = null
        var videoInfo: VideoInfo? = null
        var footer: Footer? = null
        var image: ImageInfo? = null
        var fields: List<Field>? = null
    }

    override fun build(data: MessageBuilder) {
        with (props) {
            val embed = embed(
                url,
                title,
                description,
                type,
                timestamp,
                color,
                thumbnail,
                provider,
                author,
                videoInfo,
                footer,
                image,
                fields
            )

            data.setEmbeds(data.embeds + embed)
        }
    }

    companion object : LeafFactory<Embed, Props> {
        fun MessageEmbed.toComponent(): Builder {

            return Builder { data ->
                data.setEmbeds(data.embeds + this)
            }
        }

        fun LambdaBuilder<in Embed>.embed(init: Props.() -> Unit) = + create(init)

        override fun create(init: Props.() -> Unit): Embed {
            return Embed()..init
        }
    }
}

