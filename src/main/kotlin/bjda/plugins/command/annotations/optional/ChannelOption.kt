package bjda.plugins.command.annotations.optional

import bjda.command.UnexpectedTypeException
import bjda.utils.Compare
import net.dv8tion.jda.api.entities.*
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ChannelOption(val value: Array<ChannelType> = []) {
    companion object {
        fun read(type: KClass<*>): ChannelType {
            return Compare<ChannelType>(type)
                .case<TextChannel> {ChannelType.TEXT}
                .case<PrivateChannel> {ChannelType.PRIVATE}
                .case<Category> {ChannelType.CATEGORY}
                .case<Invite.Group> {ChannelType.GROUP}
                .case<NewsChannel> {ChannelType.NEWS}
                .case<StageChannel> {ChannelType.STAGE}
                .case<VoiceChannel> {ChannelType.VOICE}
                .case<ThreadChannel> {ChannelType.GUILD_PUBLIC_THREAD}
                .default {throw UnexpectedTypeException(type) }
        }
    }
}
