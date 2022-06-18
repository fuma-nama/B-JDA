package bjda.ui.component

import bjda.ui.core.Component
import bjda.ui.core.FProps
import bjda.ui.core.RenderData
import bjda.ui.types.Init

class Text(props: Init<Props>) : Component.NoState<Text.Props>(Props(), props) {
    class Props : FProps() {
        lateinit var content: String
        lateinit var language: String
        var type: TextType = TextType.NORMAL
    }

    override fun onBuild(data: RenderData) {
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
