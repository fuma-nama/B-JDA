package bjda.ui.component

import bjda.ui.core.ElementImpl
import bjda.ui.core.IProps
import bjda.ui.core.RenderData

open class Text : ElementImpl<Text.Props>(Props()) {
    class Props : IProps() {
        var content: String? = null
        var language: String? = null
        var type: TextType = TextType.NORMAL
    }

    override fun build(data: RenderData) {
        with (props) {

            when (type) {
                TextType.NORMAL -> data.append(content)
                TextType.LINE -> data.appendLine(content)
                TextType.CODE_LINE -> data.appendCodeLine(content)
                TextType.CODE_BLOCK -> data.appendCodeBlock(content, language)
            }
        }
    }
}

enum class TextType {
    NORMAL, LINE, CODE_LINE, CODE_BLOCK
}
