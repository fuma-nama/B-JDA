package net.sonmoosans.bjdui.utils

import net.sonmoosans.bjdui.component.Fragment
import net.sonmoosans.bjdui.component.utils.Builder
import net.sonmoosans.bjda.ui.core.hooks.Context
import net.sonmoosans.bjdui.types.AnyElement
import net.sonmoosans.bjdui.types.Children
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.sonmoosans.bjda.ui.core.*
import net.sonmoosans.bjda.utils.Blocking
import net.sonmoosans.bjda.utils.LambdaBuilder
import net.sonmoosans.bjda.utils.addActionRow

open class ComponentBuilder : LambdaBuilder<AnyElement?>() {
    /**
     * Create and add Component as children
     */
    operator fun<E : ElementImpl<P>, P : CProps<C>, C : Any> ElementFactory<E, P, C>.invoke(init: P.() -> C): E {
        return create(init).also {
            + it
        }
    }

    /**
     * Create and add Component as children
     */
    operator fun<E : ElementImpl<P>, P : CProps<C>, C : Any> ElementFactory<E, P, C>.invoke(init: P.() -> Unit, children: C): E {
        return create { init(this); children }.also {
            + it
        }
    }

    /**
     * Create and add Component as children
     */
    operator fun<P : CProps<C>, C: Any> FElementConstructor<P, C>.invoke(props: P.() -> C): FElement<P> {
        val comp = this..(props)
        + comp

        return comp
    }

    /**
     * Create and add Component as children
     */
    operator fun<P : CProps<C>, C: Any> FComponentConstructor<P, C>.invoke(props: P.() -> C): FComponent<P> {
        val comp = this..(props)
        + comp

        return comp
    }

    fun<V> Context<V>.provider(value: V, children: Children) = + Provider(value, children)


    /**
     * Add children when condition is true
     *
     * you cannot add more than two children in a condition
     */
    fun `if`(condition: Boolean, children: Children) {

        if (condition) {
            val before = elements.size
            children(this)

            if (before + 1 < elements.size) {
                error("You can't add more than two elements in a condition")
            }
        } else {
            + null
        }
    }

    /**
     * Add children when condition is true. Otherwise, Add the default children
     *
     * you cannot add more than two children in a condition
     */
    fun ifElse(condition: Boolean, children: Children, default: Children) {

        if (condition) {
            val before = elements.size
            children(this)

            if (before + 1 < elements.size) {
                error("You can't add more than two elements in a condition")
            }
        } else {
            val before = elements.size
            default(this)

            if (before + 1 < elements.size) {
                error("You can't add more than two elements in a condition")
            }
        }
    }

    /**
     * Add item if condition is true, otherwise add null
     */
    inline fun <T: AnyElement> on(condition: Boolean, item: Blocking.() -> T) {
        + if (condition) item(Blocking.default) else null
    }

    /**
     * Return item if condition is false, otherwise return null
     */
    inline fun <T: AnyElement> not(condition: Boolean, item: Blocking.() -> T) {
        + if (!condition) item(Blocking.default) else null
    }

    /**
     * Add a list of items if condition is true
     *
     * Otherwise, add a list of nulls
     */
    fun where(condition: Boolean, vararg items: AnyElement?) {
        + if (condition) items else arrayOfNulls(items.size)
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