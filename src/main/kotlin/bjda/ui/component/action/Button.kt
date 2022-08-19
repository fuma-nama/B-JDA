package bjda.ui.component.action

import bjda.ui.types.Apply
import bjda.utils.LambdaBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.components.ActionComponent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.internal.interactions.component.ButtonImpl

class Button(props: Apply<Button>) : Action {

    init {
        this.apply(props)
    }

    constructor(id: String, props: Apply<Button>) : this(props) {
        this.id = id
    }

    override var id: String? = null
    var url: String? = null
    var style: ButtonStyle? = null
    var label: String? = null
    var emoji: Emoji? = null
    var disabled: Boolean = false

    fun primary(): Button {
        style = ButtonStyle.PRIMARY
        return this
    }

    fun secondary(): Button {
        style = ButtonStyle.SECONDARY
        return this
    }

    fun success(): Button {
        style = ButtonStyle.SUCCESS
        return this
    }

    fun danger(): Button {
        style = ButtonStyle.DANGER
        return this
    }

    /**
     * @param url The button's url
     */
    fun link(url: String? = null): Button {
        if (url != null) {
            this.url = url
        }

        style = ButtonStyle.LINK
        return this
    }

    override fun build(): ActionComponent {
        val style = style?: if (url != null)
            ButtonStyle.LINK
        else
            ButtonStyle.PRIMARY

        return ButtonImpl(id, label, style, url, disabled, emoji)
    }

    companion object {
        fun LambdaBuilder<in Button>.button(init: Apply<Button>) = + Button(init)

        fun LambdaBuilder<in Button>.button(label: String, init: Apply<Button>) = + Button {
            this.label = label
            init(this)
        }

        fun LambdaBuilder<in Button>.button(id: String, label: String? = null) = + Button(id) {
            this.label = label
        }

        fun LambdaBuilder<in Button>.button(id: String, label: String? = null, init: Apply<Button>) = + Button(id) {
            this.label = label
            init(this)
        }

        fun LambdaBuilder<in Button>.linkbutton(label: String, url: String, init: Apply<Button>? = null) = + Button {
            this.label = label
            link(url)

            if (init != null) {
                apply(init)
            }
        }
    }
}