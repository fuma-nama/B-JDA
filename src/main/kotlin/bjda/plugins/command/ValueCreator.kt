package bjda.plugins.command

import bjda.plugins.command.annotations.Event
import gnu.trove.map.TLongObjectMap
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf

private val resolvedField = OptionMapping::class.java.getDeclaredField("resolved")

interface ValueCreator {
    fun create(event: SlashCommandInteractionEvent): Any?
}

class EventMapper(private val param: KParameter) : ValueCreator {
    override fun create(event: SlashCommandInteractionEvent): Any? {
        if (param.hasAnnotation<Event>()) {
            return event
        }

        return null
    }
}

class OptionMapper(private val info: OptionData) : ValueCreator {
    override fun create(event: SlashCommandInteractionEvent): Any? {
        val mapping = event.getOption(info.name) ?: return null

        return when (info.type) {
                OptionType.INTEGER -> mapping.asLong
                OptionType.NUMBER -> mapping.asDouble
                OptionType.BOOLEAN -> mapping.asBoolean
                OptionType.STRING -> mapping.asString
                else -> {
                    resolvedField.isAccessible = true
                    val resolved = resolvedField.get(mapping) as TLongObjectMap<*>
                    resolved.get(mapping.asLong)
                }
            }
    }
}