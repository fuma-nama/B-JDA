package bjda.ui.core

import bjda.utils.LambdaBuilder
import bjda.utils.LambdaCreator

typealias Context<C> = LambdaCreator<C>

abstract class BasicContainer<P, C>(props: P, key: Key? = null, context: Context<C>): Container<P, Unit, C>(props, key, context)

abstract class Container<P, S : Any, C>(props: P, key: Key? = null, private val context: Context<C>): Component<P, S>(props, key) {
    fun createContext(): List<C> {
        return LambdaBuilder.build(context)
    }
}