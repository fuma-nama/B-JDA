package net.sonmoosans.bjdui.core.internal

import net.dv8tion.jda.api.utils.messages.AbstractMessageBuilder
import net.sonmoosans.bjda.utils.text
import net.sonmoosans.bjdui.component.TextStyle

typealias MessageBuilder = AbstractMessageBuilder<*, *>

fun MessageBuilder.text(
    content: String? = null,
    language: String? = null,
    type: TextStyle = TextStyle.Normal
) {
    text += when (type) {
        TextStyle.Normal -> content
        TextStyle.Line -> (content + "\n")
        TextStyle.CodeLine -> "`$content`"
        TextStyle.CodeBlock -> """
                    ```$language
                    $content
                    ```
                """.trimIndent()
    }
}