package bjda.plugins.slashcommand.annotations

import kotlin.annotation.Retention
import kotlin.annotation.Target

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Command(val name: String, val description: String)
