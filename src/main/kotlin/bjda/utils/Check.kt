package bjda.utils

    fun allEmpty(vararg lists: Collection<*>): Boolean {
        return lists.all { it.isEmpty() }
    }

    fun notEmpty(vararg lists: Collection<*>): Boolean {
        return lists.all {it.isNotEmpty()}
    }
