package bjda.ui.utils

import bjda.ui.component.Fragment
import bjda.ui.component.utils.Builder
import bjda.ui.types.AnyElement
import bjda.utils.LambdaBuilder
import bjda.utils.LambdaList
import bjda.utils.build
import net.dv8tion.jda.api.interactions.components.ActionRow

open class ComponentBuilder : LambdaBuilder<AnyElement?>() {
    /**
     * Return item if condition is true, otherwise return null
     */
    inline fun <T: AnyElement> on(condition: Boolean, item: () -> T): T? {
        return if (condition) item() else null
    }

    /**
     * Return item if condition is false, otherwise return null
     */
    inline fun <T: AnyElement> not(condition: Boolean, item: () -> T): T? {
        return if (!condition) item() else null
    }

    /**
     * Return a list of items if condition is true
     *
     * Otherwise, return empty list
     */
    fun where(condition: Boolean, vararg items: AnyElement?): Array<out AnyElement?> {
        return if (condition) items else arrayOfNulls(items.size)
    }

    /**
     * Return a list of items if condition is true
     *
     * Otherwise, return a list filled with null with min size
     */
    fun where(condition: Boolean, items: LambdaList<AnyElement?>, min: Int): List<AnyElement?> {
        return if (condition) items.build() else List(min) { null }
    }

    /**
     * Add elements as a fragment
     */
    override operator fun Collection<AnyElement?>.unaryPlus() {
        elements += Fragment(this)
    }

    /**
     * Add elements as a fragment
     */
    override operator fun Array<out AnyElement?>.unaryPlus() {
        elements += Fragment(this)
    }

    operator fun ActionRow.unaryPlus() {
        elements += Builder {
            it.addActionRow(this)
        }
    }
}