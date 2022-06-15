package bjda.ui.core

import bjda.ui.ComponentManager

class TreeScanner(private val manager: ComponentManager) {
    private fun unmounted(comp: FComponent) {
        comp.onUnmount()

        println("unmounted $comp")
    }

    private fun mounted(comp: FComponent) {
        comp.onMount(manager)

        println("mounted $comp")
    }

    private fun reused(comp: Component<*, *>, props: Any?) {
        comp.update(props)

        println("reused $comp")
    }

    fun scan(old: Children?, new: Children?): Children? {
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
                    reused(original, rendered.props)

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