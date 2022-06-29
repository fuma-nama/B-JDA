package n1

import bjda.plugins.supercommand.SuperCommand
import bjda.plugins.supercommand.SuperCommandGroup
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.util.function.Consumer

val GameCommands = SuperCommandGroup.create("game", "Games",
    SuperCommandGroup.create("votq", "Commands for Game 'votq'",
        JoinCommand(), StartCommand(), ReclaimCommand()
    )
)

class StartCommand : SuperCommand(name = "start", description = "Start vote game") {
    override fun run() {
        val game = VoteGame.create(event.textChannel, event.user)
            ?: return error("Already has existing game in this channel")

        val ui = game.join(event.user)!!.ui

        ui.reply(event, true) {
            ui.listen(it)
        }
    }
}

class JoinCommand : SuperCommand(name = "join", description = "Join vote game") {
    val id: String by option(OptionType.STRING, "id", "Channel ID").default {
        event.channel.id
    }

    override fun run() {
        getGame(id) {game ->
            val ui = game.join(event.user)?.ui

            if (ui != null) {

                ui.reply(event, true) {
                    ui.listen(it)
                }
            } else {
                error("Has been joined the game")
            }
        }
    }
}

class ReclaimCommand : SuperCommand(name = "reclaim", description = "Create a another one dashboard if you loss it") {
    val id: String by option(OptionType.STRING, "id", "Channel ID").default {
        event.channel.id
    }

    override fun run() {
        getGame(id) {game ->
            val player = game.players.find {
                it.user == event.user
            }

            if (player != null) {
                player.ui.reply(event, true) {
                    player.ui.listen(it)
                }
            } else {
                error("You haven't joined the Game")
            }
        }
    }
}

fun SuperCommand.getGame(id: String, handler: Consumer<VoteGame>) {
    val game = VoteGame.getGame(id)?: return error("Game doesn't exists")
    handler.accept(game)
}
