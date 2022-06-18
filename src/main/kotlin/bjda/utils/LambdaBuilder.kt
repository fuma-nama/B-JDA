package bjda.utils

typealias LambdaList<C> = LambdaBuilder<C>.() -> Unit

open class LambdaBuilder<C> {
    private val elements = ArrayList<C>()

    operator fun Collection<C>.unaryPlus() {
        elements += this
    }

    operator fun C.unaryPlus() {
        elements += this
    }

    operator fun Array<C>.unaryPlus() {
        elements += this
    }

    fun build(): List<C> {
        return elements
    }

    companion object {
        fun <C> build(child: LambdaList<C>): List<C> {
            val builder = LambdaBuilder<C>()
            child(builder)
            return builder.build()
        }
    }
}