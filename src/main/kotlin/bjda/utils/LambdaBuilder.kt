package bjda.utils

typealias LambdaList<C> = LambdaBuilder<C>.() -> Unit

@DslMarker
annotation class DslBuilder

fun <C>(LambdaBuilder<C>.() -> Unit).build(): List<C> {
    val builder = LambdaBuilder<C>()
    this(builder)

    return builder.build()
}

fun interface Convert<out C> {
    fun convert(): C
}

fun<C> Collection<Convert<C>>.convert(): Collection<C> {
    return this.map {
        it.convert()
    }
}

/**
 * Used for Lambda Builder
 */
@DslBuilder
class Blocking {
    companion object {
        val default = Blocking()
    }
}

@DslBuilder
open class LambdaBuilder<C> {
    val elements = ArrayList<C>()

    open operator fun Convert<C>.unaryPlus() {
        + this.convert()
    }

    open operator fun Array<Convert<C>>.unaryPlus() {
        + this.map {
            it.convert()
        }
    }

    open operator fun Collection<Convert<C>>.unaryMinus() {
        + this.map {
            it.convert()
        }
    }

    open operator fun Array<out C>.unaryPlus() {
        elements += this
    }

    open operator fun Collection<C>.unaryPlus() {
        elements += this
    }

    operator fun<T : C> T.unaryPlus(): T {
        elements += this as C
        return this
    }

    /**
     * Add element if condition is true
     */
    @Deprecated("you can use a normal If loop instead", replaceWith = ReplaceWith("if"))
    fun addIf(condition: Boolean, item: () -> C) {
        if (condition) {
            elements += item()
        }
    }

    fun build(): List<C> {
        return elements
    }
}