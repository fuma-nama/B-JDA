package net.sonmoosans.bjdui.component.utils

import net.sonmoosans.bjdui.component.action.Action
import net.sonmoosans.bjdui.component.action.toAction
import net.sonmoosans.bjda.utils.Convert
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.internal.interactions.component.ButtonImpl
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl
import net.dv8tion.jda.internal.interactions.component.TextInputImpl

fun menu(
    id: String,
    placeholder: String? = null,
    minValues: Int = 1,
    maxValues: Int = 1,
    disabled: Boolean = false,
    options: List<SelectOption>? = null,
): MenuAction {
    return MenuAction(
        SelectMenuImpl(id, placeholder, minValues, maxValues, disabled, options)
    )
}

class MenuAction(base: SelectMenu): SelectMenu by base, Convert<Action> {
    override fun convert(): Action {
        return this.toAction()
    }
}

fun primary(id: String? = null, label: String = "", disabled: Boolean = false, emoji: Emoji? = null): ButtonAction {
    return ButtonAction(id, null, label, disabled, emoji, ButtonStyle.PRIMARY)
}

fun secondary(id: String? = null, label: String = "", disabled: Boolean = false, emoji: Emoji? = null): ButtonAction {
    return ButtonAction(id, null, label, disabled, emoji, ButtonStyle.SECONDARY)
}

fun danger(id: String? = null, label: String = "", disabled: Boolean = false, emoji: Emoji? = null): ButtonAction {
    return ButtonAction(id, null, label, disabled, emoji, ButtonStyle.DANGER)
}

fun success(id: String? = null, label: String = "", disabled: Boolean = false, emoji: Emoji? = null): ButtonAction {
    return ButtonAction(id, null, label, disabled, emoji, ButtonStyle.SUCCESS)
}

fun link(url: String? = null, label: String = "", disabled: Boolean = false, emoji: Emoji? = null): ButtonAction {
    return ButtonAction(null, url, label, disabled, emoji, ButtonStyle.LINK)
}

class ButtonAction(
    id: String? = null,
    url: String? = null,
    label: String = "",
    disabled: Boolean = false,
    emoji: Emoji? = null,
    style: ButtonStyle = ButtonStyle.PRIMARY)

    : ButtonImpl(id, label, style, url, disabled, emoji), Convert<Action> {

    override fun convert(): Action {
        return this.toAction()
    }
}

fun input(
    id: String,
    label: String,
    style: TextInputStyle = TextInputStyle.SHORT,
    minLength: Int = -1,
    maxLength: Int = -1,
    required: Boolean = true,
    value: String? = null,
    placeholder: String? = null
): InputAction {
    return InputAction(
        TextInputImpl(
            id,
            style,
            label,
            minLength,
            maxLength,
            required,
            value,
            placeholder
        )
    )
}

class InputAction(base: TextInput) : TextInput by base, Convert<Action> {
    override fun convert(): Action {
        return this.toAction()
    }
}