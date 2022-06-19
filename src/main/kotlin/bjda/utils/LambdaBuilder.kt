package bjda.utils

typealias LambdaList<C> = LambdaBuilder<C>.() -> Unit

fun <C>(LambdaBuilder<C>.() -> Unit).build(): List<C> {
    val builder = LambdaBuilder<C>()
    this(builder)

    return builder.build()
}

open class LambdaBuilder<C> {
    protected val elements = ArrayList<C>()

    operator fun Collection<C>.unaryPlus() {
        elements += this
    }

    operator fun<T : C> T.unaryPlus(): T {
        elements += this as C
        return this
    }

    operator fun Array<C>.unaryPlus() {
        elements += this
    }

    fun build(): List<C> {
        return elements
    }
}