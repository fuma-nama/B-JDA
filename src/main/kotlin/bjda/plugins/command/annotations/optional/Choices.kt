package bjda.plugins.command.annotations.optional

import bjda.ui.exceptions.UnexpectedTypeException
import bjda.utils.Compare
import net.dv8tion.jda.api.interactions.commands.Command
import kotlin.reflect.KType

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Choices(vararg val value: Choice) {
    annotation class Choice(val name: String, val value: String)

    companion object {
        fun map(choices: Choices, type: KType): List<Command.Choice> {

            return choices.value.map {

                when (val value = read(it.value, type)) {
                    is Long -> Command.Choice(it.name, value)
                    is Double -> Command.Choice(it.name, value)
                    is String -> Command.Choice(it.name, value)
                    else -> throw UnexpectedTypeException(type)
                }
            }
        }

        private fun read(value: String, type: KType): Any {
            return Compare<Any>(type)
                .case<Int> {value.toLong()}
                .case<Long> {value.toLong()}
                .case<Number> {value.toDouble()}
                .default {value}
        }
    }
}
