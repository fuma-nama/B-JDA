package net.sonmoosans.bjdui.component.utils

import net.sonmoosans.bjda.ui.core.ElementImpl
import net.sonmoosans.bjda.ui.core.IProps
import net.sonmoosans.bjdui.core.internal.MessageBuilder
import net.sonmoosans.bjda.utils.LambdaBuilder

/**
 * Call the listener when building the component
 */
class Builder(builder: (MessageBuilder) -> Unit) : ElementImpl<Builder.Props>(Props(builder)) {
    class Props(val builder: (MessageBuilder) -> Unit) : IProps()

    override fun build(data: MessageBuilder) = props.builder(data)
}

fun LambdaBuilder<in Builder>.build(builder: (MessageBuilder) -> Unit) =+ Builder(builder)