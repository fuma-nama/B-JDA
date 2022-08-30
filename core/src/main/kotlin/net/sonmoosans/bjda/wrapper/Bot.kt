package net.sonmoosans.bjda.wrapper

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent

fun bot(token: String, vararg intents: GatewayIntent, init: JDABuilder.() -> Unit): JDABuilder {
    return JDABuilder.create(intents.toList()).setToken(token).apply(init)
}

fun bot(token: String, intents: Int, init: JDABuilder.() -> Unit): JDABuilder {
    return JDABuilder.create(GatewayIntent.getIntents(intents)).setToken(token).apply(init)
}

fun botDefault(token: String, init: JDABuilder.() -> Unit): JDABuilder {
    return JDABuilder.createDefault(token).apply(init)
}

fun botDefault(token: String, vararg intents: GatewayIntent, init: JDABuilder.() -> Unit): JDABuilder {
    return JDABuilder.createDefault(token, intents.toList()).apply(init)
}

fun botLight(token: String, init: JDABuilder.() -> Unit): JDABuilder {
    return JDABuilder.createLight(token).apply(init)
}

fun botLight(token: String, vararg intents: GatewayIntent, init: JDABuilder.() -> Unit): JDABuilder {
    return JDABuilder.createLight(token, intents.toList()).apply(init)
}