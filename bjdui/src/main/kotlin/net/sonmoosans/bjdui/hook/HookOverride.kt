package net.sonmoosans.bjdui.hook

import net.dv8tion.jda.api.utils.messages.MessageEditData
import net.sonmoosans.bjda.ui.hook.UpdateHook

/**
 * Change the behavior of an Update Hook
 */
class HookOverride(
    val base: UpdateHook,

    /**
     * When enabled, this Hook will never be updated
     */
    val alwaysIgnore: Boolean = false,

    /**
     * What to do when hook is unmounted
     */
    val onUnmount: Actions = Actions.Unmount,

    /**
     * What to do when hook is destroyed
     */
    val onDestroy: Actions = Actions.DeleteOriginal,
): UpdateHook() {

    constructor(
        alwaysIgnore: Boolean = false,

        onUnmount: Actions = Actions.Unmount,

        onDestroy: Actions = Actions.DeleteOriginal,

        hook: () -> UpdateHook
    ) : this(hook(), alwaysIgnore, onUnmount, onDestroy)

    override var ui by base::ui

    override fun isIgnored(data: HookData): Boolean {

        return alwaysIgnore || base.isIgnored(data)
    }

    override fun onUpdate(message: MessageEditData, data: HookData) = base.onUpdate(message, data)

    override fun listen() = base.listen()

    override fun unmount() {
        doAction(onUnmount)
    }

    override fun onDestroy() {
        doAction(onDestroy)
    }

    private fun doAction(action: Actions) {
        when (action) {
            Actions.Unmount -> base.unmount()
            Actions.DeleteOriginal -> base.onDestroy()
            Actions.DestroyUI -> ui.destroy()
        }
    }
}

enum class Actions {
    DestroyUI, Unmount, DeleteOriginal
}