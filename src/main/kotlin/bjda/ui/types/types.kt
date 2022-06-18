package bjda.ui.types

import bjda.ui.core.Component
import bjda.utils.LambdaList

typealias FComponent = Component<*, *>
typealias RenderContext =  Array<FComponent?>
typealias Elements = LambdaList<FComponent?>
typealias Key = Any

typealias Init<T> = T.() -> Unit