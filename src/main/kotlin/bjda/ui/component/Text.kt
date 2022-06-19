package bjda.ui.component

import bjda.ui.core.Component
import bjda.ui.core.IProps
import bjda.ui.core.RenderData

class Text : Component.NoState<Text.Props>(Props()) {
    class Props : IProps() {
        lateinit var content: String
        lateinit var language: String
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
