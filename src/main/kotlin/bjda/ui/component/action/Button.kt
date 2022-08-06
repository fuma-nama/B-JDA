package bjda.ui.component.action

import bjda.ui.core.apply
import bjda.ui.types.Apply
import bjda.utils.LambdaBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.components.ActionComponent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.internal.interactions.component.ButtonImpl

class Button(props: Apply<Props>) : Action {
    private val props = Props().apply(props)
    override val id by this.props::id

    constructor(id: String, props: Apply<Props>) : this(props) {
        this.props.id = id
    }

    class Props(
        var id: String? = null,
        var url: String? = null,
        var style: ButtonStyle? = null,
        var label: String? = null,
        var emoji: Emoji? = null,
        var disabled: Boolean = false
    )

    override fun build(): ActionComponent {
        with (props) {
            val style = style?: if (url != null)
                ButtonStyle.LINK
            else
                ButtonStyle.PRIMARY

            return ButtonImpl(id, label, style, url, disabled, emoji)
        }
    }

    companion object {
        fun LambdaBuilder<in Button>.button(init: Props.() -> Unit) = + Button(init)

        fun LambdaBuilder<in Button>.button(label: String, init: Props.() -> Unit) = + Button {
            this.label = label
            init(this)
        }

        fun LambdaBuilder<in Button>.button(label: String? = null, id: String, init: Props.() -> Unit) = + Button(id) {
            this.label = label
            init(this)
        }
    }
}