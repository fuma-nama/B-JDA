package bjda.utils

typealias LambdaList<C> = LambdaBuilder<C>.() -> Unit

fun <C>(LambdaBuilder<C>.() -> Unit).build(): List<C> {
    val builder = LambdaBuilder<C>()
    this(builder)

    return builder.build()
}

open class LambdaBuilder<C> {
    val elements = ArrayList<C>()

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
    fun where(condition: Boolean, vararg items: C): Array<out C?> {
        return if (condition) items else arrayOfNulls<Any?>(items.size) as Array<C?>
    }

    /**
     * Return a list of items if condition is true
     *
     * Otherwise, return a list filled with null with min size
     */
    fun where(condition: Boolean, items: LambdaList<C>, min: Int): List<C?> {
        return if (condition) items.build() else List(min) { null }
    }

    fun build(): List<C> {
        return elements
    }
}