package bjda.plugins.ui.modal

import bjda.plugins.ui.AutoReply
import bjda.plugins.ui.UIEvent
import bjda.plugins.ui.event.ModalListener
import bjda.ui.component.action.Action
import bjda.ui.core.UI
import bjda.ui.core.hooks.IHook
import bjda.ui.core.init
import bjda.ui.types.AnyComponent
import bjda.ui.types.Init
import bjda.utils.LambdaList
import bjda.utils.build
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.Modal
import net.dv8tion.jda.internal.interactions.component.ModalImpl

typealias EventHandler = (ModalInteractionEvent) -> AutoReply
/**
 * Same usage as Component API but specified for Modal
 *
 * Cannot update modal with state or forceUpdate
 *
 * Modal ID is generated per instance
 *
 * Note: use it as Hook to enable AutoReply
 */
class Form(val id: String = UIEvent.createId(),
           props: Init<Props>
): ModalListener, IHook<Modal> {
    private var ui: UI? = null

    class Props {
        lateinit var title: String
        lateinit var rows: LambdaList<Input>
        lateinit var onSubmit: EventHandler
    }

    init {
        UIEvent.listen(id, this)
    }

    val props = Props().init(props)

    fun create(): ModalImpl {
        with (props) {
            val rows = rows.build().map {
                ActionRow.of(it.build())
            }

            return ModalImpl(id, title, rows)
        }
    }

    override fun onSubmit(event: ModalInteractionEvent) {
        val then = props.onSubmit(event)

        val ui = this.ui?: return

        then.reply(ui, event)
    }

    override fun onCreate(component: AnyComponent): Modal {
        this.ui = component.ui

        return create()
    }
}

data class Input(val items: LambdaList<Action>) {
    fun build(): List<ItemComponent> {
        return items.build().map {
            it.build()
        }
    }
}
