package bjda.ui.component

import bjda.ui.component.utils.Builder
import bjda.ui.core.ElementImpl
import bjda.ui.core.IProps
import bjda.ui.core.RenderData
import bjda.utils.embed
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.MessageEmbed.*
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

    override fun build(data: RenderData) {
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

            data.addEmbeds(embed)
        }
    }

    companion object {
        fun MessageEmbed.toComponent(): Builder {

            return Builder { data ->
                data.addEmbeds(this)
            }
        }
    }
}

