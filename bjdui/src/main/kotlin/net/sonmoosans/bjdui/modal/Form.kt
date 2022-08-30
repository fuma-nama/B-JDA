package net.sonmoosans.bjdui.modal

import net.sonmoosans.bjda.plugins.ui.UIEvent
import net.sonmoosans.bjdui.component.row.Row
import net.sonmoosans.bjda.ui.core.hooks.Delegate
import net.sonmoosans.bjda.ui.core.hooks.IHook
import net.sonmoosans.bjdui.types.AnyComponent
import net.sonmoosans.bjdui.types.Apply
import net.sonmoosans.bjda.utils.LambdaList
import net.sonmoosans.bjda.utils.build
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.Modal
import net.dv8tion.jda.internal.interactions.component.ModalImpl

/**
 * Same usage as Component API but specified for Modal
 *
 * Cannot update modal with state or forceUpdate
 *
 * Modal ID is generated per instance
 *
 * Note: use it as Hook to attach it to Component Lifecycle
 */
open class Form(
    val id: String = UIEvent.createId(),
    val create: Apply<Props>
) {
    class Props {
        lateinit var title: String
        lateinit var onSubmit: (ModalInteractionEvent) -> Unit
        lateinit var render: LambdaList<Row>
    }
    val props = Props().apply(create)

    /**
     * Listen submit events
     */
    fun listen() {
        UIEvent.listen(id, props.onSubmit)
    }

    /**
     * Build a modal and set up its listeners
     */
    fun create(listen: Boolean = true): ModalImpl {
        if (listen) {
            this.listen()
        }

        with (props) {
            val rows = render.build().map {
                it.build()
            }

            return ModalImpl(id, title, rows)
        }
    }

    fun destroy() {
        UIEvent.modals.remove(id)
    }

    companion object {
        fun AnyComponent.form(id: String = UIEvent.createId(), create: Apply<Props>): Delegate<Modal> {
            val form = Form(id, create)
            val hook = ModalHook(form)

            return Delegate { this use hook }
        }

        class ModalHook(private val form: Form) : IHook<Modal> {
            override fun onCreate(component: AnyComponent, initial: Boolean): Modal {
                if (initial) {
                    form.listen()
                }

                return form.create(listen = false)
            }

            override fun onDestroy() {
                form.destroy()
            }
        }
    }
}