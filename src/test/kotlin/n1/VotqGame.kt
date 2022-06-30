package n1

import bjda.ui.component.*
import bjda.ui.core.Component.Companion.rangeTo
import bjda.ui.core.UI
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

class Question(val title: String, answers: List<String>) {
    val answers: List<Answer> = answers.map {
        Answer(it)
    }

    fun getAnswer(name: String): Answer? {
        return answers.toList().find {it.name == name}
    }
}

fun WaitingPlayersPanel(message: String = "Waiting Other Players..."): Embed {
    return Embed()..{
        title = message
        description = "Please wait :P"
    }
}

class VoteGame(val id: String, private val owner: User) {
    val players = ArrayList<Player>()
    private var current = 0
    var started = false
    private val winScore = 10

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

    private fun updateInfo() {
        for (player in players) {
            player.ui.updateComponent()
            player.ui.updateHooks()
        }
    }

    fun start() {
        started = true
        displayMessage(

            "Game Started!",
            "Wait for few seconds",
            ::next
        )
    }

    private fun displayMessage(title: String, description: String, then: () -> Unit) = runBlocking {
        for (player in players) {
            player.ui.switchTo(
                Embed()..{
                    this.title = title
                    this.description = description
                    color = Color.GREEN
                }
            )
        }

        launch {
            delay(3000L)
            then()
        }
    }

    private fun skipCurrent(current: Player) {
        displayMessage(
            "${current.user.name} skipped his round!",
            "The question will be written by other player...",
            ::next
        )
    }

    private fun canEnd(): Boolean {
        return players.any { it.score >= winScore }
    }

    private fun end() {
        val winners = players.filter {
            it.score == winScore
        }

        for (player in players) {
            player.ui.switchTo(
                EndPanel {
                    this.player = player
                    this.winners = winners
                    this.isWinner = player.score == winScore
                }
            )
        }

        games.remove(id)
    }

    fun next() {
        if (canEnd()) {
            return end()
        }

        if (current == players.size) {
            current = 0
        }

        val asker = players[current]

        for (player in players) {
            val ui = if (player == asker) {
                AskPanel()..{
                    onAsk = ::ask
                    onSkip = {
                        skipCurrent(asker)
                    }
                }
            } else {
                WaitingPlayersPanel("Its ${asker.user.name}'s turn!")
            }

            player.ui.switchTo(ui)
        }

        current++
    }

    private fun confirmResult(best: List<Answer>?, answered: Map<Player, String>) {
        var confirmed = 0

        val onConfirm: () -> Unit = {
            confirmed++

            if (confirmed == players.size) {
                next()
            }
        }

        for ((player, chose) in answered) {
            val correct = best != null && best.any {
                it.name == chose
            }

            if (correct) {
                player.score++
            }

            player.ui.switchTo(
                ResultPanel {
                    this.answers = best
                    this.isCorrect = correct
                    this.score = player.score
                    this.onConfirm = onConfirm
                }
            )
        }
    }

    private fun ask(question: Question) {
        val answered = hashMapOf<Player, String>()

        fun onFinish() {
            val best = question.answers
                .toList()
                .groupBy { it.votes }
                .maxByOrNull { it.key }!!.value

            confirmResult(
                if (best.size == question.answers.size) {
                    null
                } else {
                    best
                },
                answered
            )
        }

        fun onAnswer(player: Player, chose: String) {
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
                    this.onAnswer = ::onAnswer
                }
            )
        }
    }

    companion object {
        private val games = hashMapOf<String, VoteGame>()

        fun create(channel: TextChannel, owner: User): VoteGame? {

            val exist = games.containsKey(channel.id)

            if (!exist) {
                val game = VoteGame(channel.id, owner)
                games[channel.id] = game

                return game
            }

            return null
        }

        fun getGame(id: String): VoteGame? {
            return games[id]
        }
    }
}
