package net.sonmoosans.bjda.ui.core

import net.sonmoosans.bjdui.core.internal.MessageBuilder
import net.sonmoosans.bjdui.types.AnyProps
import net.sonmoosans.bjdui.types.Children

typealias FComponentBody<P> = FComponent<P>.() -> Children

fun interface FComponentConstructor<P : CProps<C>, C : Any> {
    /**
     * Create a component with props
     */
    operator fun rangeTo(props: P.() -> C): FComponent<P>
}

class FComponent<P: AnyProps>(
    props: P,
    private val component: FComponentBody<P>
) : Component<P>(props) {
    private lateinit var render: Children
    var build: ((data: MessageBuilder) -> Unit)? = null
    var unmount: (() -> Unit)? = null
    var receiveProps: ((prev: P, next: P) -> Unit)? = null

    override fun onMount() {
        render = component(this)
    }

    override fun onUnmount() {
        this.unmount?.invoke()
    }

    override fun build(data: MessageBuilder) {
        this.build?.invoke(data)
    }

    override fun onReceiveProps(prev: P, next: P) {
        this.receiveProps?.invoke(prev, next)
    }

    override fun onRender(): Children {
        return render
    }

    companion object {

        fun<P: CProps<C>, C : Any> component(props: () -> P, component: FComponentBody<P>): FComponentConstructor<P, C> {
            return FComponentConstructor {init ->
                FComponent(props().init(init), component)
            }
        }
        fun component(component: FComponentBody<IProps>): FComponentConstructor<IProps, Unit> {
            return FComponentConstructor {init ->
                FComponent(IProps().init(init), component)
            }
        }
    }
}