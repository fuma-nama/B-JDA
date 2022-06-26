package bjda.ui.component.action

import bjda.ui.core.init
import bjda.ui.types.Init
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.internal.interactions.component.ButtonImpl

class Button(props: Init<Props>) : Action {
    constructor(id: String, props: Init<Props>) : this(props) {
        this.props.id = id
    }

    private val props = Props().init(props)

    class Props {
        lateinit var id: String
        var style: ButtonStyle = ButtonStyle.PRIMARY
        var label: String? = null
        var emoji: Emoji? = null
        var url: String? = null
        var disabled: Boolean = false
    }

    override fun build(): ItemComponent {
        with (props) {
            return ButtonImpl(id, label, style, url, disabled, emoji)
        }
    }
}