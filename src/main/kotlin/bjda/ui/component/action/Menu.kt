package bjda.ui.component.action

import bjda.plugins.ui.hook.MenuSelect
import bjda.ui.core.init
import bjda.ui.types.Init
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl

class Menu(val id: String, props: Init<Props>) : Action {
    constructor(onSelect: MenuSelect, props: Init<Props>) : this(onSelect.id, props)

    private val props = Props().init(props)

    class Props {
        var placeholder: String? = null
        var minValues: Int = 1
        var maxValues: Int = 1
        var disabled: Boolean = false
        lateinit var options: List<SelectOption>
    }

    override fun build(): ItemComponent {
        with (props) {
            val max = maxValues.coerceAtMost(options.size)

            return SelectMenuImpl(id, placeholder, minValues, max, disabled, options)
        }
    }

    companion object {
        fun createOptions(selected: String? = null, vararg pairs: Pair<String, String>): List<SelectOption> {
            return pairs.map {(key, value) ->
                SelectOption.of(key, value).withDefault(value == selected)
            }
        }
    }
}