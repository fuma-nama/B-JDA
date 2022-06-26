package bjda.plugins.ui.modal

import bjda.plugins.ui.UIEvent
import bjda.plugins.ui.hook.event.ModalListener
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
): ModalListener {

    class Props {
        lateinit var title: String
        lateinit var rows: LambdaList<Input>

        lateinit var onSubmit: (ModalInteractionEvent) -> Unit
    }

    val props = Props().init(props)

    fun create(): ModalImpl {
        UIEvent.listen(id, this)

        with (props) {
            val rows = rows.build().map {
                ActionRow.of(it.build())
            }

            return ModalImpl(id, title, rows)
        }
    }

    fun destroy() {
        UIEvent.modals.remove(id)
    }

    override fun onSubmit(event: ModalInteractionEvent) {
        props.onSubmit(event)
    }

    companion object {
        fun AnyComponent.form(props: Init<Props>): ModalCreator {
            val form = Form(props = props)
            val hook = ModalCreator(form)
            this.use(hook)

            return hook
        }

        class ModalCreator(private val form: Form) : IHook<Unit> {
            operator fun getValue(parent: Any, property: Any): Modal {
                return form.create()
            }

            override fun getValue() = Unit

            override fun onCreate(component: AnyComponent) = Unit

            override fun onDestroy() {
                form.destroy()
            }
        }
    }
}

data class Input(val items: LambdaList<Action>) {
    fun build(): List<ItemComponent> {
        return items.build().map {
            it.build()
        }
    }
}
