package bjda.ui.core

import bjda.ui.types.AnyComponent
import bjda.ui.types.AnyElement
import bjda.ui.types.ComponentTree
import bjda.ui.types.ElementTree

abstract class ComponentTreeScanner(val parent: AnyElement) {
    protected abstract fun unmounted(comp: AnyElement)

    protected abstract fun mounted(comp: AnyComponent): AnyElement

    protected abstract fun<P : IProps> reused(comp: Component<out P, *>.Element, props: P)

    /**
     * Compare the snapshot and rendered components
     *
     * and notify updates to children
     */
    fun scan(snapshot: ElementTree?, rendered: ComponentTree): ElementTree {
        if (snapshot == null) {
            return rendered.map {comp ->
                comp?.let {mounted(it)}
            }.toTypedArray()
        }

        if (snapshot.isEmpty() && rendered.isEmpty())
            return emptyArray()

        val keyMap = snapshot
            .filter { it?.key != null }
            .associateBy { it?.key }

        return rendered.mapIndexed{ i, comp ->
            val key = comp?.props?.key

            val original =
                if (key != null) keyMap[key]
                else snapshot.getOrNull(i)

            if (comp == null) {
                if (original != null) {
                    unmounted(original)
                }

                null
            } else {
                if (original != null && comp::class == original.getType()) {
                    reused(original, comp.props)

                    original
                } else {
                    mounted(comp)
                }
            }
        }.toTypedArray()
    }
}