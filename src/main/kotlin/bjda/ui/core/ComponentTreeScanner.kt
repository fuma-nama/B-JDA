package bjda.ui.core

import bjda.ui.types.AnyElement
import bjda.ui.types.AnyProps
import bjda.ui.types.ComponentTree

private val AnyElement.key: Any?
    get() {
        return props.key
    }

class ComponentTreeScannerImpl(val ui: UI) : ComponentTreeScanner() {
    override fun unmounted(comp: AnyElement) {
        comp.unmount()
    }

    override fun mounted(comp: AnyElement, parent: AnyElement) {
        comp.mount(parent, this.ui)
    }

    override fun <P : AnyProps> reused(comp: Element<out P>, props: P) {
        comp.receiveProps(props)
    }
}

abstract class ComponentTreeScanner {
    protected abstract fun unmounted(comp: AnyElement)

    protected abstract fun mounted(comp: AnyElement, parent: AnyElement)

    protected abstract fun<P : AnyProps> reused(comp: Element<out P>, props: P)

    /**
     * Compare the snapshot and rendered components
     *
     * and notify updates to children
     */
    open fun scan(parent: AnyElement, snapshot: ComponentTree?, rendered: ComponentTree): ComponentTree {

        if (snapshot == null) {
            return rendered.map {comp ->
                if (comp != null) {
                    mounted(comp, parent)
                }

                comp
            }.toTypedArray()
        }

        if (snapshot.isEmpty() && rendered.isEmpty())
            return emptyArray()

        val keyMap = snapshot
            .filter { it?.key != null }
            .associateBy { it?.key }

        return rendered.mapIndexed{ i, comp ->
            val key = comp?.key

            val original =
                if (key != null) keyMap[key]
                else snapshot.getOrNull(i)

            if (comp == null) {
                if (original != null) {
                    unmounted(original)
                }

                null
            } else {
                if (original != null && comp::class == original::class) {
                    reused(original, comp.props)

                    original
                } else {
                    mounted(comp, parent)

                    comp
                }
            }
        }.toTypedArray()
    }
}