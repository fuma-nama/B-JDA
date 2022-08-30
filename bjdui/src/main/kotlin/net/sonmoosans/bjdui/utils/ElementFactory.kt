package net.sonmoosans.bjdui.utils

import net.sonmoosans.bjda.ui.core.CProps
import net.sonmoosans.bjda.ui.core.Element
import net.sonmoosans.bjdui.types.Children

/**
 * Element Factory that has no children
 */
typealias LeafFactory<E, P> = ElementFactory<E, P, Unit>

fun interface ElementFactory<E: Element<P>, P : CProps<C>, C : Any> {
    operator fun rangeTo(init: P.() -> C): E {
        return create(init)
    }

    fun create(init: P.() -> Unit, children: C): E {

        return create { init(this); children }
    }

    fun create(init: P.() -> C): E
}

fun interface AncestorFactory<E : Element<P>, P : CProps<Children>> : ElementFactory<E, P, Children>
