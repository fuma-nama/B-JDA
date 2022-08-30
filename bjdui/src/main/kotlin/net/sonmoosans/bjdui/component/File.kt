package net.sonmoosans.bjdui.component

import net.sonmoosans.bjdui.core.internal.MessageBuilder
import net.sonmoosans.bjdui.utils.LeafFactory
import net.sonmoosans.bjda.utils.LambdaBuilder
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder
import net.sonmoosans.bjda.ui.core.ElementImpl
import net.sonmoosans.bjda.ui.core.IProps
import net.sonmoosans.bjda.ui.core.rangeTo
import java.io.InputStream

class File : ElementImpl<File.Props>(Props()) {
    class Props : IProps() {
        lateinit var data: InputStream
        lateinit var name: String
        var spoiler: Boolean = false
    }

    override fun build(data: MessageBuilder) {
        with(props) {
            var file = FileUpload.fromData(this.data, name)

            if (spoiler) {
                file = file.asSpoiler()
            }

            when (data) {
                is MessageCreateBuilder -> data.addFiles(file)
                is MessageEditBuilder -> data.setAttachments(data.attachments + file)
                else -> {}
            }
        }
    }

    companion object : LeafFactory<File, Props> {
        override fun create(init: Props.() -> Unit): File = File()..init

        fun LambdaBuilder<in File>.file(init: Props.() -> Unit) = +create(init)
    }
}