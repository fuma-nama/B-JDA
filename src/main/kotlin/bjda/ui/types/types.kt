package bjda.ui.types

import bjda.ui.core.CProps
import bjda.ui.core.Component
import bjda.ui.core.ComponentBuilder
import bjda.ui.core.Element
import bjda.ui.core.hooks.Context

typealias AnyElement = Element<*>
typealias AnyComponent = Component<*>
typealias Children = ComponentBuilder.() -> Unit
typealias ComponentTree = Array<out AnyElement?>
typealias Key = Any
typealias Apply<T> = T.() -> Unit
typealias AnyProps = CProps<*>
typealias ContextMap = HashMap<Context<*>, Any?>