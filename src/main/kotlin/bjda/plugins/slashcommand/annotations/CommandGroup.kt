package bjda.plugins.slashcommand.annotations

import kotlin.annotation.Retention
import kotlin.annotation.Target

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandGroup(val name: String, val description: String)