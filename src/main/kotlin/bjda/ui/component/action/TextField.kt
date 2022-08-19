package bjda.ui.component.action

import bjda.ui.core.apply
import bjda.ui.types.Apply
import bjda.utils.Convert
import bjda.utils.LambdaBuilder
import net.dv8tion.jda.api.interactions.components.ActionComponent
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.internal.interactions.component.TextInputImpl

class TextField(override val id: String, props: Apply<TextField>) : Action {
    var style: TextInputStyle = TextInputStyle.SHORT
    lateinit var label: String
    var minLength: Int = -1
    var maxLength: Int = -1
    var required: Boolean = true
    var value: String? = null
    var placeholder: String? = null

    init {
        apply(props)
    }

    override fun build(): ActionComponent {
        return TextInputImpl(
            id,
            style,
            label,
            minLength,
            maxLength,
            required,
            value,
            placeholder
        )
    }

    companion object {
        fun LambdaBuilder<in TextField>.input(id: String, init: Apply<TextField>) = + TextField(id, init)
    }
}