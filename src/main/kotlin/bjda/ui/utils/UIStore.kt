package bjda.ui.utils

import bjda.ui.core.UI

open class UIStore<T>: HashMap<T, UI>() {
    fun getUI(key: T, createUI: () -> UI): UI {
        var ui = this[key]

        if (ui == null) {
            ui = createUI()
            this[key] = ui
        }

        return ui
    }
}