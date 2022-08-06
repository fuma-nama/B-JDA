package bjda.ui.component

import bjda.plugins.ui.hook.ButtonClick
import bjda.ui.component.action.Button
import bjda.ui.component.row.Row
import bjda.ui.core.*
import bjda.ui.types.Children
import bjda.ui.types.ComponentTree
import bjda.ui.utils.AncestorFactory
import bjda.ui.utils.ComponentBuilder
import bjda.utils.LambdaBuilder

class Pager : Component<Pager.Props>(Props()) {
    lateinit var pages: ComponentTree
    var page = useState(props.defaultPage)

    class Props: CProps<Children>() {
        var defaultPage: Int = 0
    }

    override fun onReceiveProps(prev: Props, next: Props) {
        page update next.defaultPage
    }

    override fun onMount() {
        pages = parseChildren(props.children)
    }

    private val onNextPage = ButtonClick {
        page.update(it, page.get() + 1)
    }

    private val onPrevPage = ButtonClick {
        page.update(it, page.get() - 1)
    }

    override fun onRender(): Children {
        val page = page.get()

        return {
            + pages.getOrNull(page)
            + Row(
                Button(id = use(onPrevPage)) {
                    id = use(onPrevPage)
                    label = "<-"
                    disabled = page <= 0
                },

                Button(id = use(onNextPage)) {
                    label = "->"
                    disabled = page >= pages.size - 1
                }
            )
        }
    }

    companion object : AncestorFactory<Pager, Props> {
        fun LambdaBuilder<in Pager>.pager(page: Int = 0, children: Children) =
            + Pager.create({defaultPage = page}, children)

        override fun create(init: Props.() -> Children): Pager {
            return Pager()..init
        }
    }
}