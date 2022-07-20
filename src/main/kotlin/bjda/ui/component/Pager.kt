package bjda.ui.component

import bjda.plugins.ui.hook.ButtonClick
import bjda.ui.component.action.Button
import bjda.ui.core.*
import bjda.ui.types.AnyElement
import bjda.ui.types.Children

class Pager : Component<Pager.Props>(Props()) {
    lateinit var pages: Array<AnyElement?>
    var page: Int by useState(props.defaultPage)

    override fun onReceiveProps(prev: Props, next: Props) {
        page = next.defaultPage
    }

    class Props: CProps<Children>() {
        var defaultPage: Int = 0
    }

    override fun onMount() {
        pages = parseChildren(props.children)
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
                    disabled = page >= pages.size - 1
                }
            }
        }
    }
}