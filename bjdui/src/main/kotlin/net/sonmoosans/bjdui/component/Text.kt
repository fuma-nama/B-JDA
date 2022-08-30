package net.sonmoosans.bjdui.component

import net.sonmoosans.bjda.ui.core.ElementImpl
import net.sonmoosans.bjda.ui.core.IProps
import net.sonmoosans.bjdui.core.internal.MessageBuilder
import net.sonmoosans.bjda.ui.core.rangeTo
import net.sonmoosans.bjdui.utils.LeafFactory
import net.sonmoosans.bjda.utils.LambdaBuilder
import net.sonmoosans.bjda.utils.text

open class Text : ElementImpl<Text.Props> {
    constructor() : super(Props())
    constructor(content: String) : super(Props().apply { this.content = content })

    class Props : IProps() {
        var content: String? = null
        var language: String? = null
        var style: TextStyle = TextStyle.Normal

        fun normal() {
            style = TextStyle.Normal
        }

        fun codeBlock() {
            style = TextStyle.CodeBlock
        }

        fun codeLine() {
            style = TextStyle.CodeLine
        }

        fun line() {
            style = TextStyle.Line
        }
    }

    override fun build(data: MessageBuilder) {
        with(props) {

            data.text += when (style) {
                TextStyle.Normal -> content
                TextStyle.Line -> content + "\n"
                TextStyle.CodeLine -> "`$content`"
                TextStyle.CodeBlock -> """
                    ```${language.orEmpty()} 
                    $content
                    ```
                """.trimIndent()
            }
        }
    }

    companion object : LeafFactory<Text, Props> {
        fun LambdaBuilder<in Text>.text(content: String) = +Text(content)
        fun LambdaBuilder<in Text>.text(init: Props.() -> Unit) = +Text()..init
        fun LambdaBuilder<in Text>.text(content: String, init: Props.() -> Unit) = +Text(content)..init

        override fun create(init: Props.() -> Unit): Text {
            return Text()..init
        }
    }
}

enum class TextStyle {
    Normal, Line, CodeLine, CodeBlock
}
