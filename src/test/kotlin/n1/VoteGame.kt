package n1

import bjda.plugins.supercommand.SuperCommand
import bjda.ui.component.*
import bjda.ui.core.Component.Companion.rangeTo
import bjda.ui.core.UI
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import java.awt.Color

data class Player(val user: User, val isOwner: Boolean) {
    val ui: UI = UI(
        UI.Option(updateHooks = false)
    )
    var score = 0
}

data class Answer(val name: String, var votes: Int = 0)

class Question(val title: String, answers: Pair<String, String>) {
    val answers: Pair<Answer, Answer> = Answer(answers.first) to Answer(answers.second)

    fun getAnswer(name: String): Answer? {
        return answers.toList().find {it.name == name}
    }
}

class StartCommand : SuperCommand(group = "vote", subgroup = "game", name = "start", description = "Start vote game") {
    override fun run() {
        val game = VoteGame.create(event.textChannel, event.user)
            ?: return error("Already has existing game in this channel")

        val ui = game.join(event.user)!!.ui

        ui.reply(event, true) {
            ui.listen(it)
        }
    }
}

class JoinCommand : SuperCommand(group = "vote", subgroup = "game", name = "join", description = "Join vote game") {
    override fun run() {
        val ui = VoteGame.join(event.textChannel.id, event.user)?.ui ?: return error("Has been joined the game")

        ui.reply(event, true) {
            ui.listen(it)
        }
    }
}

fun WaitingPlayersPanel(): Embed {
    return Embed()..{
        title = "Waiting Other Players..."
        description = "Please wait :P"
    }
}

class VoteGame(private val channel: TextChannel, val owner: User) {
    val players = ArrayList<Player>()
    private var current = 0
    var started = false

    fun join(user: User): Player? {
        if (started || players.any { it.user.id == user.id }) return null

        val player = Player(user, user == owner)
        players.add(player)

        player.ui.switchTo(
            WaitingPanel()..{
                game = this@VoteGame
                this.player = player
            }
        )

        updateInfo()
        return player
    }

    fun leave(player: Player): MessageEmbed {
        players.remove(player)
        updateInfo()

        return EmbedBuilder()
            .setTitle("${player.user.name} Left the game")
            .setColor(Color.RED)
            .build()
    }

    companion object {
        private val games = hashMapOf<String, VoteGame>()

        fun create(channel: TextChannel, owner: User): VoteGame? {

            val exist = games.containsKey(channel.id)

            if (!exist) {
                val game = VoteGame(channel, owner)
                games[channel.id] = game

                return game
            }

            return null
        }

        fun join(id: String, user: User): Player? {
            val game = games[id]?: return null

            return game.join(user)
        }
    }

    private fun updateInfo() {
        for (player in players) {
            player.ui.updateComponent()
            player.ui.updateHooks()
        }
    }

    fun start() {
        started = true
        channel.sendMessageEmbeds(
            EmbedBuilder()
                .setTitle("Game Started!")
                .setColor(Color.GREEN)
                .build()
        ).queue()

        next()
    }

    fun next() {
        val asker = players[current]
        channel.sendMessage("${asker.user.name}'s turn!").queue()
        updateInfo()

        for (player in players) {
            val ui = if (player == asker) {
                AskPanel()..{
                    onAsk = ::ask
                }
            } else {
                WaitingPlayersPanel()
            }

            player.ui.switchTo(ui)
        }

        current++
    }

    private fun confirmResult(best: Answer, answered: Map<Player, String>) {
        var confirmed = 0

        val onConfirm: () -> Unit = {
            confirmed++

            if (confirmed == players.size) {
                next()
            }
        }

        for ((player, chose) in answered) {
            val correct = chose == best.name

            if (correct) {
                player.score++
            }

            player.ui.switchTo(
                ResultPanel {
                    this.answer = best
                    this.isCorrect = correct
                    this.score = player.score
                    this.onConfirm = onConfirm
                }
            )
        }
    }

    private fun ask(question: Question) {
        val answered = hashMapOf<Player, String>()

        val onFinish = {
            val best = question.answers
                .toList()
                .maxByOrNull { it.votes }!!

            confirmResult(best, answered)
        }

        val onAnswer = {player: Player, chose: String ->
            question.getAnswer(chose)?.let {
                it.votes++
            }

            answered[player] = chose

            if (answered.size == players.size) {
                onFinish()
            }
        }

        for (player in players) {
            player.ui.switchTo(
                AnswerPanel {
                    this.question = question
                    this.player = player
                    this.onAnswer = onAnswer
                }
            )
        }
    }
}
