package bjda.ui.types

import bjda.ui.core.Component
import bjda.ui.core.hooks.Context
import bjda.utils.LambdaList

typealias AnyComponent = Component<*, *>
typealias Children = LambdaList<AnyComponent?>
typealias AnyElement = Component<*, *>.Element
typealias ElementTree = Array<AnyElement?>
typealias ComponentTree = Array<AnyComponent?>
typealias Key = Any
typealias Init<T> = T.() -> Unit
typealias ContextMap = HashMap<Context<*>, Any?>