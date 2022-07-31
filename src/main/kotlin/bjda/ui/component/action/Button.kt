package bjda.ui.component.action

import bjda.ui.core.apply
import bjda.ui.types.Apply
import bjda.utils.Convert
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
        fun primary(id: String? = null, label: String = "", disabled: Boolean = false, emoji: Emoji? = null): Impl {
            return Impl(id, null, label, disabled, emoji, ButtonStyle.PRIMARY)
        }

        fun secondary(id: String? = null, label: String = "", disabled: Boolean = false, emoji: Emoji? = null): Impl {
            return Impl(id, null, label, disabled, emoji, ButtonStyle.SECONDARY)
        }

        fun danger(id: String? = null, label: String = "", disabled: Boolean = false, emoji: Emoji? = null): Impl {
            return Impl(id, null, label, disabled, emoji, ButtonStyle.DANGER)
        }

        fun success(id: String? = null, label: String = "", disabled: Boolean = false, emoji: Emoji? = null): Impl {
            return Impl(id, null, label, disabled, emoji, ButtonStyle.SUCCESS)
        }

        fun link(url: String? = null, label: String = "", disabled: Boolean = false, emoji: Emoji? = null): Impl  {
            return Impl(null, url, label, disabled, emoji, ButtonStyle.LINK)
        }

        class Impl(
            id: String? = null,
            url: String? = null,
            label: String = "",
            disabled: Boolean = false,
            emoji: Emoji? = null,
            style: ButtonStyle = ButtonStyle.PRIMARY)

            : ButtonImpl(id, label, style, url, disabled, emoji), Convert<Action> {

            override fun convert(): Action {
                return this.toAction()
            }
        }
    }
}