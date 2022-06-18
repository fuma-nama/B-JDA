package bjda.ui.component.hooks

import bjda.ui.core.*
import bjda.ui.types.FComponent
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
class Provider<T>(props: Props<T>, children: LambdaList<FComponent?>) : Component.NoState<Provider.Props<T>>(props) {
    data class Props<T>(val value: T): FProps()

    override fun onBuild(data: RenderData) {
        super.onBuild(data)
    }
}