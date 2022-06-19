package bjda.ui.core.hooks

import bjda.ui.core.*
import bjda.ui.types.AnyComponent
import bjda.utils.LambdaList

fun getProvider() {

}


//TODO: inline Props constructor
val a = TestP(Provider.Props("Hello"))

class TestP<T>(props: Provider.Props<T>) {

}
/**
 * Pass variable to children components
 */
class Provider<T>(props: Props<T>, children: LambdaList<AnyComponent?>) : Component.NoState<Provider.Props<T>>(props) {
    data class Props<T>(val value: T): IProps()

}