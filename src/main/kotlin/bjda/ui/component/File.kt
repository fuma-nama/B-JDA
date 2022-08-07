package bjda.ui.component

import bjda.ui.core.ElementImpl
import bjda.ui.core.IProps
import bjda.ui.core.internal.RenderData
import bjda.ui.core.rangeTo
import bjda.ui.utils.LeafFactory
import bjda.utils.LambdaBuilder
import net.dv8tion.jda.api.utils.AttachmentOption
import java.io.InputStream

class File : ElementImpl<File.Props>(Props()) {
    class Props : IProps() {
        lateinit var data: InputStream
        lateinit var name: String
        var options: Array<AttachmentOption> = emptyArray()
    }

    override fun build(data: RenderData) {
        with (props) {
            data.addFile(this.data, name, * options)
        }
    }

    companion object : LeafFactory<File, Props> {
        override fun create(init: Props.() -> Unit): File = File()..init
        fun LambdaBuilder<in File>.file(init: Props.() -> Unit) = + create(init)
    }
}