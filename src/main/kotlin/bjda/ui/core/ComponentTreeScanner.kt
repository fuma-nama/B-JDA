package bjda.ui.core

import bjda.ui.types.FComponent
import bjda.ui.types.RenderContext

abstract class ComponentTreeScanner {
    protected abstract fun unmounted(comp: FComponent)

    protected abstract fun mounted(comp: FComponent)

    protected abstract fun<P : FProps> reused(comp: Component<P, *>, props: P)

    /**
     * Compare the snapshot and rendered components
     *
     * and notify updates to children
     */
    fun scan(old: RenderContext?, new: RenderContext?): RenderContext? {
        if (new == null) {
            old?.forEach {
                if (it != null)
                    unmounted(it)
            }

            return old
        }
        if (old == null) {
            new.forEach {
                if (it != null)
                    mounted(it)
            }
            return new
        }

        val keyMap = old
            .filter { it?.key != null }
            .associateBy { it?.key }

        return new.mapIndexed{i, rendered ->
            val original =
                if (rendered?.key != null) keyMap[rendered.key]
                else old.getOrNull(i)

            if (rendered == null) {
                if (original != null) {
                    unmounted(original)
                }

                null
            } else {
                if (original != null && isSameComponent(rendered, original)) {
                    reused(original as Component<FProps, *>, rendered.props)

                    original
                } else {
                    mounted(rendered)

                    rendered
                }
            }
        }.toTypedArray()
    }

    private fun isSameComponent(first: FComponent, second: FComponent): Boolean {
        return first::class == second::class
    }
}