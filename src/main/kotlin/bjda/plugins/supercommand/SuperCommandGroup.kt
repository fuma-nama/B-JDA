package bjda.plugins.supercommand

abstract class SuperCommandGroup(override val name: String, override val description: String): SuperNode {
    open fun groups(): Array<out SuperCommandGroup>? = null
    open fun commands(): Array<out SuperCommand>? = null

    companion object {
        fun create(name: String, description: String, vararg commands: SuperCommand): SuperCommandGroup {

            return object : SuperCommandGroup(name, description) {
                override fun commands(): Array<out SuperCommand> {
                    return commands
                }
            }
        }

        fun create(name: String, description: String, vararg groups: SuperCommandGroup): SuperCommandGroup {

            return object : SuperCommandGroup(name, description) {
                override fun groups(): Array<out SuperCommandGroup>? {
                    return groups
                }
            }
        }
    }
}