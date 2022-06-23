package bjda.plugins.slashcommand.annotations.optional

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Range(val from: String = "", val to: String = "")
