package bjda.plugins.command.annotations

import net.dv8tion.jda.api.interactions.commands.OptionType

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Param(
    val name: String,
    val description: String = "No Description",
    val type: OptionType = OptionType.UNKNOWN,
    val required: Boolean = true
)