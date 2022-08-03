package bjda.ui.utils

import bjda.ui.core.UI

/**
 * For production, you might use other cache libraries.
 *
 * And set an expiry time for each entry
 */
open class UIStore<T>: HashMap<T, UI>() {
    /**
     * get UI instance or put the default value
     */
    fun getUI(key: T, createUI: () -> UI): UI {
        return this.getOrPut(key, createUI)
    }

    /**
     * Put and Destroy the old instance with the new one
     *
     * @return Destroyed UI
     */
    fun putUI(key: T, new: UI): UI? {
        val ui = this.put(key, new)
        ui?.destroy()

        return ui
    }

    /**
     * Replace and Destroy the old instance with the new one
     *
     * @return Destroyed UI
     */
    fun replaceUI(key: T, new: UI): UI? {
        val ui = this.replace(key, new)
        ui?.destroy()

        return ui
    }

    /**
     * Remove and Destroy UI
     *
     * @return True if the key exists
     */
    fun destroy(key: T): Boolean {
        val ui = this.remove(key)
            ?: return false

        ui.destroy()

        return true
    }
}