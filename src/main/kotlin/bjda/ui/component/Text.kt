package bjda.ui.component

import bjda.ui.core.ElementImpl
import bjda.ui.core.IProps
import bjda.ui.core.RenderData
import bjda.ui.core.rangeTo
import bjda.ui.utils.ComponentBuilder
import bjda.ui.utils.LeafFactory
import bjda.utils.LambdaBuilder

open class Text : ElementImpl<Text.Props> {
    constructor() : super(Props())
    constructor(content: String) : super(Props().apply { this.content = content })

    class Props : IProps() {
        var content: String? = null
        var language: String? = null
        var style: TextStyle = TextStyle.NORMAL
    }

    override fun build(data: RenderData) {
        with (props) {

            when (style) {
                TextStyle.NORMAL -> data.append(content)
                TextStyle.LINE -> data.appendLine(content)
                TextStyle.CODE_LINE -> data.appendCodeLine(content)
                TextStyle.CODE_BLOCK -> data.appendCodeBlock(content, language)
            }
        }
    }

    companion object : LeafFactory<Text, Props> {
        fun LambdaBuilder<in Text>.text(init: Props.() -> Unit) = + Text()..init
        fun LambdaBuilder<in Text>.text(content: String, init: Props.() -> Unit) = + Text(content)..init

        override fun create(init: Props.() -> Unit): Text {
            return Text()..init
        }
    }
}

enum class TextStyle {
    NORMAL, LINE, CODE_LINE, CODE_BLOCK
}
