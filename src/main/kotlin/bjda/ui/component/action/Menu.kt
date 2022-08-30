package bjda.ui.component.action

import bjda.ui.core.apply
import bjda.ui.types.Apply
import bjda.utils.LambdaBuilder
import net.dv8tion.jda.api.interactions.components.ActionComponent
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl

fun createOptions(selected: String? = null, vararg pairs: Pair<String, String>): List<SelectOption> {
    return pairs.map {(key, value) ->
        SelectOption.of(key, value).withDefault(value == selected)
    }
}

class Menu(props: Apply<Menu>) : Action {
    override lateinit var id: String
    var placeholder: String? = null
    var minValues: Int = 1
    var maxValues: Int = 1
    var disabled: Boolean = false
    var options = mutableListOf<SelectOption>()

    init {
        apply(props)
    }

    constructor(id: String, props: Apply<Menu>) : this(props) {
        this.id = id
    }

    fun option(key: String, value: String, mapper: (SelectOption.() -> SelectOption)? = null) {
        val option = SelectOption.of(key, value)

        options += mapper?.invoke(option)?: option
    }

    override fun build(): ActionComponent {
        val max = maxValues.coerceAtMost(options.size)

        return SelectMenuImpl(id, placeholder, minValues, max, disabled, options)
    }

    companion object {
        fun LambdaBuilder<in Menu>.menu(init: Menu.() -> Unit) = + Menu(init)
        fun LambdaBuilder<in Menu>.menu(id: String, init: Menu.() -> Unit) = + Menu(id, init)
    }
}
