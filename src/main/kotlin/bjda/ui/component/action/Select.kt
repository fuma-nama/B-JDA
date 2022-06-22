package bjda.ui.component.action

import bjda.ui.core.init
import bjda.ui.types.Init
import bjda.utils.LambdaList
import bjda.utils.build
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl

class Select(val id: String, props: Init<Props>) : Action {
    private val props = Props().init(props)

    class Props {
        var placeholder: String? = null
        var minValues: Int = 1
        var maxValues: Int = 1
        var disabled: Boolean = false
        lateinit var options: LambdaList<SelectOption>
    }

    override fun build(): ItemComponent {
        with (props) {
            val options = options.build()

            val min = minValues.coerceAtMost(options.size)
            val max = maxValues.coerceAtMost(options.size)
            return SelectMenuImpl(id, placeholder, min, max, disabled, options)
        }
    }
}