/*
import bjda.plugins.ui.hook.ButtonClick
import bjda.ui.component.Embed
import bjda.ui.component.Group
import bjda.ui.component.Row
import bjda.ui.component.action.Button
import bjda.ui.core.Component
import bjda.ui.core.Component.Companion.minus
import bjda.ui.core.Component.Companion.rangeTo
import bjda.ui.core.IProps
import bjda.ui.types.Children
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed.Field
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import java.awt.Color

class VoteGame(private val channel: TextChannel, val owner: User) {
    val players = ArrayList<Player>()

    private var current = 0
    var started = false

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

        fun join(id: String, user: User): Dashboard? {
            val game = games[id]?: return null

            if (game.started || game.players.any { it.user.id == user.id }) return null

            with (game) {
                val player = Player(user)

                players.add(player)

                control.forceUpdate()
                return player.ui
            }
        }
    }

    fun start() {
        started = true
        next()
    }

    fun next() {
        val player = players[current]
        channel.sendMessage("${player.user.name}'s turn!").queue()

        player.ui

        current++
    }

    inner class Player(val user: User) {
        val ui: Dashboard = Dashboard(this)
    }

    data class Answer(val name: String, var votes: Int = 0)
    data class Question(val title: String, val answers: Pair<Answer, Answer>)

    inner class ControlUI : Component<IProps, Unit>(IProps()) {
        private val onStart = ButtonClick {event ->
            event.replyEmbeds(
                EmbedBuilder()
                    .setTitle("Game Started!")
                    .setColor(Color.GREEN)
                    .build()
            ).queue()

            start()
        }

        override fun onRender(): Children {
            return {
                Embed()..{
                    title = "Waiting Players"
                    description = "${players.size} Players joined"
                    fields = players.map {player ->
                        Field(player.user.name, "", false)
                    }
                }

                Row()-{
                    + Button {
                        id = use(onStart)
                        label = "Start Game"
                        disabled = players.size < 2
                    }
                }
            }
        }
    }

    inner class Dashboard(val player: Player) : Component<IProps, Unit>(IProps()) {
        val onLeave = ButtonClick {event ->

        }

        override fun onRender(): Children {
            return {
                + on(started) {

                }

                + not(started) {
                    if (player.user == owner) ControlUI()
                    else waitingPanel()
                }
            }
        }
    }

    fun Dashboard.waitingPanel(): Group {
        return Group {
            + Embed()..{
                title = "Joined the Game"
                description = "Waiting to start..."
            }

            + Row()-{
                + Button {
                    id = use(onLeave)
                    label = "Leave"
                    style = ButtonStyle.DANGER
                }
            }
        }
    }
}

 */