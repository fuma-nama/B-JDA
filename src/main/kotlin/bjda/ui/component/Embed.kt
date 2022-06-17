package bjda.ui.component

import bjda.ui.core.BasicComponent
import bjda.ui.core.RenderData
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color
import java.time.temporal.TemporalAccessor

class Embed(props: Props) : BasicComponent<Embed.Props>(props) {
    data class Props(
        val title: String? = null,
        val titleUrl: String? = null,
        val description: String? = null,
        val author: String? = null,
        val authorUrl: String? = null,
        val authorIcon: String? = null,
        val footer: String? = null,
        val footerIcon: String? = null,
        val thumbnail: String? = null,
        val image: String? = null,
        val timestamp: TemporalAccessor? = null,
        val color: Color? = null
    )

    override fun onBuild(data: RenderData) {
        with (props) {

            data.addEmbeds(
                EmbedBuilder()
                    .setTitle(title, titleUrl)
                    .setDescription(description)
                    .setAuthor(author, authorUrl, authorIcon)
                    .setFooter(footer, footerIcon)
                    .setColor(color)
                    .setThumbnail(thumbnail)
                    .setImage(image)
                    .setTimestamp(timestamp)
                    .build()
            )
        }
    }
}

