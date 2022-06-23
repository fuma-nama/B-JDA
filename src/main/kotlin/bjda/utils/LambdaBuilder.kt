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

    operator fun LambdaList<C>.unaryPlus() {
        elements += this.build()
    }

    operator fun<T : C> T.unaryPlus(): T {
        elements += this as C
        return this
    }

    /**
     * Add element if condition is true
     */
    fun addIf(condition: Boolean, item: () -> C) {
        if (condition) {
            elements += item()
        }
    }

    /**
     * Return item if condition is true, otherwise return null
     */
    inline fun <T: C> on(condition: Boolean, item: () -> T): T? {
        return if (condition) item() else null
    }

    /**
     * Return item if condition is false, otherwise return null
     */
    inline fun <T: C> not(condition: Boolean, item: () -> T): T? {
        return if (!condition) item() else null
    }

    /**
     * Return a list of items if condition is true
     *
     * Otherwise, return empty list
     */
    fun where(condition: Boolean, items: LambdaList<C>): List<C> {
        return if (condition) items.build() else emptyList()
    }

    /**
     * Return a list of items if condition is true
     *
     * Otherwise, return a list filled with null with min size
     */
    fun where(condition: Boolean, items: LambdaList<C>, min: Int): List<C?> {
        return if (condition) items.build() else List(min) { null }
    }

    operator fun Array<C>.unaryPlus() {
        elements += this
    }

    fun build(): List<C> {
        return elements
    }
}