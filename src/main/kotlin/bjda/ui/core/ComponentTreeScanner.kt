package bjda.ui.core

import bjda.ui.types.AnyComponent
import bjda.ui.types.AnyElement
import bjda.ui.types.ComponentTree
import bjda.ui.types.ElementTree

abstract class ComponentTreeScanner {
    protected abstract fun unmounted(comp: AnyElement)

    protected abstract fun mounted(comp: AnyComponent): AnyElement

    protected abstract fun<P : IProps> reused(comp: Component<out P, *>.Element, props: P)

    /**
     * Compare the snapshot and rendered components
     *
     * and notify updates to children
     */
    fun scan(snapshot: ElementTree?, rendered: ComponentTree?): ElementTree? {
        if (rendered == null) {
            snapshot?.forEach {
                if (it != null)
                    unmounted(it)
            }

            return snapshot
        }
        if (snapshot == null) {
            return rendered.map {
                if (it != null)
                    mounted(it)
                else null
            }.toTypedArray()
        }

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
                if (original != null && comp::class == original.getComponentType()) {
                    reused(original, comp.props)

                    original
                } else {
                    mounted(comp)
                }
            }
        }.toTypedArray()
    }
}