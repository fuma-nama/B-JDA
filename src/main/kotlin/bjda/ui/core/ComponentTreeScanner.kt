package bjda.ui.core

import bjda.ui.types.AnyComponent
import bjda.ui.types.ComponentTree

private val AnyComponent.key: Any?
    get() {
        return props.key
    }

abstract class ComponentTreeScanner {
    protected abstract fun unmounted(comp: AnyComponent)

    protected abstract fun mounted(comp: AnyComponent)

    protected abstract fun<P : IProps> reused(comp: Component<out P>, props: P)

    /**
     * Compare the snapshot and rendered components
     *
     * and notify updates to children
     */
    fun scan(snapshot: ComponentTree?, rendered: ComponentTree): ComponentTree {
        if (snapshot == null) {
            return rendered.map {comp ->
                if (comp != null) {
                    mounted(comp)
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
                    mounted(comp)

                    comp
                }
            }
        }.toTypedArray()
    }
}