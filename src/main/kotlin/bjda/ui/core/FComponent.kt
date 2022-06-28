package bjda.ui.core

import bjda.ui.types.Children
import bjda.ui.types.Init

typealias FComponentBody<P, S> = FComponent<P, S>.() -> Children
typealias FComponentConstructor<P, S> = (Init<P>) -> FComponent<P, S>

class FComponent<P: IProps, S: Any>(
    props: P,
    component: FComponentBody<P, S>
) : Component<P, S>(props) {
    val children = component(this)

    companion object {
        fun<P: IProps, S: Any> create(props: () -> P, component: FComponentBody<P, S>): FComponentConstructor<P, S> {
            return {init ->
                FComponent(props(), component)..init
            }
        }

        fun<P: IProps> noState(defaultProps: () -> P, component: FComponentBody<P, Unit>): FComponentConstructor<P, Unit> {
            return create(defaultProps, component)
        }
    }

    override fun onRender(): Children {
        return children
    }
}