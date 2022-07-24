package bjda.ui.core

import bjda.ui.types.AnyProps
import bjda.ui.types.Children

typealias FComponentBody<P> = FComponent<P>.() -> Children
typealias FComponentConstructor<P, C> = (props: P.() -> C) -> FComponent<P>

class FComponent<P: AnyProps>(
    props: P,
    private val component: FComponentBody<P>
) : Component<P>(props) {
    lateinit var children: Children

    override fun onMount() {
        children = component(this)
    }

    companion object {
        fun<P: CProps<C>, C : Any> component(props: () -> P, component: FComponentBody<P>): FComponentConstructor<P, C> {
            return {init ->
                FComponent(props().init(init), component)
            }
        }

        fun component(component: FComponentBody<IProps>): FComponentConstructor<IProps, Unit> {
            return {init ->
                FComponent(IProps().init(init), component)
            }
        }
    }

    override fun onRender(): Children {
        return children
    }
}