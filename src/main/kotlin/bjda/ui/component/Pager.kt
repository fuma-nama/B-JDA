package bjda.ui.component

import bjda.plugins.ui.hook.ButtonClick
import bjda.ui.component.action.Button
import bjda.ui.core.CProps
import bjda.ui.core.Component
import bjda.ui.types.AnyComponent
import bjda.ui.types.Children
import bjda.utils.LambdaList
import bjda.utils.build

class Pager : Component<Pager.Props>(Props()) {
    var page: Int by useState(props::defaultPage)

    class Props: CProps<LambdaList<AnyComponent>>() {
        lateinit var pages: Array<AnyComponent>

        var defaultPage: Int = 0

        override var children: LambdaList<AnyComponent> = {}
            set(value) {
                pages = value.build().toTypedArray()

                field = value
            }
    }

    private val onNextPage = ButtonClick {
        page++

        ui.edit(it)
    }

    private val onPrevPage = ButtonClick {
        page--

        ui.edit(it)
    }

    override fun onRender(): Children {
        with (props) {

            return {
                + pages.getOrNull(page)
                + Row()-{
                    + Button(id = use(onPrevPage)) {
                        id = use(onPrevPage)
                        label = "<-"
                        disabled = page <= 0
                    }

                    + Button(id = use(onNextPage)) {
                        label = "->"
                        disabled = page >= props.pages.size - 1
                    }
                }
            }
        }

    }
}