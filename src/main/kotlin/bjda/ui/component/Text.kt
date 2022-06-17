package bjda.ui.component

import bjda.ui.core.BasicComponent
import bjda.ui.core.RenderData

class Text(props: Props) : BasicComponent<Text.Props>(props) {
    data class Props(val content: String, val type: TextType = TextType.NORMAL, val language: String = "")

    override fun onBuild(data: RenderData) {
        val (content, type, language) = props

        when (type) {
            TextType.NORMAL -> data.append(content)
            TextType.LINE -> data.appendLine(content)
            TextType.CODE_LINE -> data.appendCodeLine(content)
            TextType.CODE_BLOCK -> data.appendCodeBlock(content, language)
        }
    }
}

enum class TextType {
    NORMAL, LINE, CODE_LINE, CODE_BLOCK
}
