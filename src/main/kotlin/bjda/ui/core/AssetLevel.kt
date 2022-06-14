package bjda.ui.core

class AssetLevel {
    var state: Any? = null
    private val children = HashMap<Int, AssetLevel>()

    fun getOrCreate(key: Int): AssetLevel {
        var asset = children[key]

        if (asset == null) {
            asset = AssetLevel()
            children[key] = asset
        }

        return asset
    }
}