package bjda.ui.component

import bjda.plugins.ui.hook.ButtonClick
import bjda.ui.component.action.Button
import bjda.ui.core.CProps
import bjda.ui.core.Component
import bjda.ui.types.AnyComponent
import bjda.ui.types.Children
import bjda.utils.LambdaList
import bjda.utils.build

class Pager : Component<Pager.Props, Pager.State>(Props()) {
    class Props: CProps<LambdaList<AnyComponent>>() {
        lateinit var pages: Array<AnyComponent>

        var defaultPage: Int = 0

        override var children: LambdaList<AnyComponent> = {}
            set(value) {
                pages = value.build().toTypedArray()

                field = value
            }
    }

    class State(var page: Int)

    override fun onMount() {
        this.state = State(
            props.defaultPage
        )
    }

    private val onNextPage = ButtonClick {
        updateState {
            page++
        }

        ui.edit(it)
    }

    private val onPrevPage = ButtonClick {
        updateState {
            page--
        }

        ui.edit(it)
    }

    override fun onRender(): Children {
        with (props) {

            return {
                + pages.getOrNull(state.page)
                + Row()-{
                    + Button(id = use(onNextPage)) {
                        label = "->"
                        disabled = state.page >= props.pages.size - 1
                    }

                    + Button(id = use(onPrevPage)) {
                        id = use(onPrevPage)
                        label = "<-"
                        disabled = state.page <= 0
                    }
                }
            }
        }

    }
}