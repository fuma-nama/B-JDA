package bjda.ui.component

import bjda.ui.core.*
import bjda.ui.core.internal.RenderData
import bjda.ui.types.ComponentTree
import bjda.ui.utils.ElementFactory
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

class FileAsync : ElementImpl<FileAsync.Props>(Props()) {
    lateinit var cache: FileData

    class Props : CProps<() -> FileData>()

    override fun render(): ComponentTree? {
        cache = props.children()

        return null
    }

    override fun build(data: RenderData) {
        with (cache) {
            data.addFile(this.data, name, * options)
        }
    }

    class FileData(
        val data: InputStream,
        val name: String,
        val options: Array<out AttachmentOption>
    )

    companion object : ElementFactory<FileAsync, Props, () -> FileData> {
        fun data(
            data: InputStream,
            name: String,
            vararg options: AttachmentOption
        ) = FileData(data, name, options)

        /**
         * Get and Add the file only when rendering the component
         */
        fun LambdaBuilder<in FileAsync>.fileAsync(init: () -> FileData) = + FileAsync()-init

        override fun create(init: Props.() -> () -> FileData) = FileAsync()..init
    }
}