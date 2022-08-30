package net.sonmoosans.bjdui.types

import net.sonmoosans.bjda.ui.core.CProps
import net.sonmoosans.bjda.ui.core.Component
import net.sonmoosans.bjdui.utils.ComponentBuilder
import net.sonmoosans.bjda.ui.core.Element
import net.sonmoosans.bjda.ui.core.hooks.Context

typealias AnyElement = Element<*>
typealias AnyComponent = Component<*>
typealias Children = ComponentBuilder.() -> Unit
typealias ComponentTree = Array<out AnyElement?>
typealias Key = Any
typealias Apply<T> = T.() -> Unit
typealias AnyProps = CProps<*>
typealias ContextMap = HashMap<Context<*>, Any?>