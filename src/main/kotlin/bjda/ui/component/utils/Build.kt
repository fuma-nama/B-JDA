package bjda.ui.component.utils

import bjda.ui.core.ElementImpl
import bjda.ui.core.IProps
import bjda.ui.core.internal.MessageBuilder
import bjda.utils.LambdaBuilder

/**
 * Call the listener when building the component
 */
class Builder(builder: (MessageBuilder) -> Unit) : ElementImpl<Builder.Props>(Props(builder)) {
    class Props(val builder: (MessageBuilder) -> Unit) : IProps()

    override fun build(data: MessageBuilder) = props.builder(data)
}

fun LambdaBuilder<in Builder>.build(builder: (MessageBuilder) -> Unit) =+ Builder(builder)