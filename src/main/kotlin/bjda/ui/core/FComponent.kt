package bjda.ui.core

import bjda.ui.types.Children
import bjda.ui.types.Init

typealias FComponentBody<P> = FComponent<P>.() -> Children
typealias FComponentConstructor<P> = (props: Init<P>) -> FComponent<P>

class FComponent<P: IProps>(
    props: P,
    private val component: FComponentBody<P>
) : Component<P>(props) {
    lateinit var children: Children

    override fun onMount() {
        children = component(this)
    }

    companion object {
        fun<P: IProps> create(props: () -> P, component: FComponentBody<P>): FComponentConstructor<P> {
            return {init ->
                FComponent(props().init(init), component)
            }
        }
    }

    override fun onRender(): Children {
        return children
    }
}