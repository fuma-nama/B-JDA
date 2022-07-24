package bjda.utils

class Translation(words: Map<String, String>): Map<String, String> by words {
    val groups = HashMap<String, Translation>()

    override operator fun get(key: String): String {
        return this[key]
    }

    operator fun invoke(group: String): Translation {
        return this.groups[group]!!
    }

    operator fun invoke(vararg groups: Pair<String, Translation>): Translation {
        this.groups.putAll(groups)
        return this
    }

    companion object {
        fun group(vararg words: Pair<String, String>): Translation {
            return Translation(mapOf(*words))
        }
    }
}