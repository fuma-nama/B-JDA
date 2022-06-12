package bjda.plugins.command

import bjda.plugins.command.annotations.Param
import bjda.plugins.command.annotations.optional.AutoComplete
import bjda.plugins.command.annotations.optional.ChannelOption
import bjda.plugins.command.annotations.optional.Choices
import bjda.plugins.command.annotations.optional.Range
import bjda.utils.Compare
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmName

class OptionParser {
    companion object {
        fun from(info: Param, param: KParameter): OptionData {
            val classifier = param.type.classifier as KClass<*>
            val data = with(info) {
                var parsedType = type

                if (type == OptionType.UNKNOWN)
                    parsedType = generateOptionType(classifier)

                OptionData(parsedType, name, description, required)
            }

            param.findAnnotation<Range>()?.let {
                with(it) {
                    with(data) {
                        if (type == OptionType.INTEGER) {
                            setMinValue(from.toLong())
                            setMaxValue(to.toLong())
                        } else {
                            setMinValue(from.toDouble())
                            setMaxValue(to.toDouble())
                        }
                    }
                }
            }

            param.findAnnotation<Choices>()?.let {
                data.addChoices(Choices.map(it, param.type))
            }

            param.findAnnotation<ChannelOption>()?.let {
                with(data) {
                    if (it.value.isEmpty()) {
                        setChannelTypes(ChannelOption.read(classifier))
                    } else {
                        setChannelTypes(*it.value)
                    }
                }
            }

            param.findAnnotation<AutoComplete>()?.let {
                data.isAutoComplete = it.value
            }

            return data
        }

        fun generateOptionType(type: KClass<*>): OptionType {

            return Compare<OptionType>(type)
                .case<String> { OptionType.STRING }
                .case<Long> { OptionType.INTEGER }
                .case<Double> { OptionType.NUMBER }
                .case<Boolean> { OptionType.BOOLEAN }
                .case<User> { OptionType.USER }
                .case<Role> { OptionType.ROLE }
                .case<Message.Attachment> { OptionType.ATTACHMENT }
                .case<Channel> { OptionType.CHANNEL }
                .case<IMentionable> { OptionType.MENTIONABLE }
                .case<Int> {
                    throw IllegalArgumentException("Integer is unsupported option type, Please use Long instead")
                }
                .case<Number> {
                    throw IllegalArgumentException("$type is unsupported option type, Please use Double instead")
                }
                .default {
                    throw IllegalArgumentException("Unsupported option type: ${type.jvmName}")
                }
        }
    }
}