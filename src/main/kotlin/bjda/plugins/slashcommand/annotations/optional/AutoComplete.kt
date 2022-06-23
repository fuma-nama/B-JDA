package bjda.plugins.slashcommand.annotations.optional

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class AutoComplete(val value: Boolean)
