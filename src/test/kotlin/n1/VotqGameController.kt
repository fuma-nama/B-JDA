package n1

import bjda.plugins.supercommand.SuperCommand
import bjda.plugins.supercommand.SuperCommandGroup
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.util.function.Consumer

val help = """
    **Commands**
    Type the command `/game votq start` to open a new game
    There's only one game per channel
    You can use `/game votq join` to join the game at current channel
    
    If you want to use the UI at another one channel, type `/game votq reclaim`
    It will open a new Dashboard for you
    
    Only the owner can stop the game with `/game votq stop`
    
    **Rules**
    Players will be able to write a question when its their turn, 
    After that, Players need to choose a answer
    
    1 score for the player who answers the most player-chosen answers
    However, if all answers have the same amount of players chosen, no one gets 1 point
    
    The game will keep running until one of the player reaches the specified win score 
""".trimIndent()

val GameCommands = SuperCommandGroup.create("game", "Games",
    SuperCommandGroup.create("votq", "Commands for Game 'votq'",
        JoinCommand(), StartCommand(), ReclaimCommand(), StopCommand(), HelpCommand()
    )
)

class HelpCommand : SuperCommand(name = "help", description = "Learn how to play Votq") {
    override fun run() {
        event.replyEmbeds(
            EmbedBuilder()
                .setTitle("How to play Votq")
                .setDescription(help)
                .build()
        ).queue()
    }
}

class StartCommand : SuperCommand(name = "start", description = "Start vote game") {
    val winScore: Int by option(OptionType.INTEGER, "win-score", "Specify the least score to win").default {
        10
    }

    override fun run() {
        val game = VoteGame.create(event.textChannel, event.user, winScore)
            ?: return error("Already has existing game in this channel")

        val ui = game.join(event.user)!!.ui

        ui.reply(event, true) {
            ui.listen(it)
        }
    }
}

class StopCommand : SuperCommand(name = "stop", description = "Stop a game") {
    override fun run() {
        getGame(event.channel.id) {game ->
            if (game.owner != event.user) {
                error("Only the owner can stop the game")
            }

            event.replyEmbeds(
                EmbedBuilder()
                    .setTitle("${event.user.name} Ended the game")
                    .build()
            ).queue()

            game.end()
        }
    }
}

class JoinCommand : SuperCommand(name = "join", description = "Join vote game") {
    override fun run() {
        getGame(event.channel.id) {game ->
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