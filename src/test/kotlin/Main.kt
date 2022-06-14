import bjda.BJDA
import bjda.plugins.command.CommandModule
import net.dv8tion.jda.api.JDABuilder
import test.MainController

fun main() {
    val jda = JDABuilder.createDefault("OTA3OTU1NzgxOTcyOTE4Mjgz.GSitoB.iCipeHkaFCFr3prGuj2GLKz17DGCxeq5zK2R-w")
        .build()
        .awaitReady()

    BJDA.create(jda)
        .install(
            CommandModule(MainController::class)
        )
}