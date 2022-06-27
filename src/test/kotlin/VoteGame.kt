import bjda.plugins.supercommand.SuperCommand
import bjda.plugins.ui.hook.ButtonClick
import bjda.ui.component.*
import bjda.ui.component.action.Button
import bjda.ui.core.Component
import bjda.ui.core.Component.Companion.minus
import bjda.ui.core.Component.Companion.rangeTo
import bjda.ui.core.IProps
import bjda.ui.core.UI
import bjda.ui.listener.InteractionUpdateHook
import bjda.ui.types.Children
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.MessageEmbed.Field
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import java.awt.Color

class VoteGame(private val channel: TextChannel, val owner: User) {
    class StartCommand : SuperCommand(group = "vote", subgroup = "game", name = "start", description = "Start vote game") {
        override fun run() {
            val game = create(event.textChannel, event.user)
                ?: return error("Already has existing game in this channel")

            val ui = game.join(event.user)

            event.reply(ui!!.build()).queue {
                ui.listen(it)
            }
        }
    }

    class JoinCommand : SuperCommand(group = "vote", subgroup = "game", name = "join", description = "Join vote game") {
        override fun run() {
            val ui = join(event.textChannel.id, event.user)?: return error("Has been joined the game")

            event.reply(ui.build()).queue {
                ui.listen(it)
            }
        }
    }

    val players = ArrayList<Player>()

    private var current = 0
    var started = false

    fun join(user: User): UI<Dashboard>? {
        if (started || players.any { it.user.id == user.id }) return null

        val player = Player(user)
        players.add(player)

        updateInfo()
        return player.ui
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

        fun join(id: String, user: User): UI<Dashboard>? {
            val game = games[id]?: return null

            return game.join(user)
        }
    }

    private fun updateInfo() {
        for (player in players) {
            player.ui.root.forceUpdate()
        }
    }

    fun start() {
        started = true
        next()
    }

    fun next() {
        val player = players[current]
        channel.sendMessage("${player.user.name}'s turn!").queue()
        updateInfo()

        player.ui

        current++
    }

    inner class Player(val user: User) {
        val ui = UI(Dashboard(this))
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
            ).queue {
                start()
            }
        }

        override fun onRender(): Children {
            return {
                + Embed()..{
                    title = "Waiting Players"
                    description = "${players.size} Players joined"
                    fields = players.map {player ->
                        Field(player.user.name, "", false)
                    }
                }

                + Row()-{
                    + Button {
                        id = use(onStart)
                        label = "Start Game"
                        disabled = players.size < 2
                    }
                }
            }
        }
    }

    enum class Status {
        ASK, ANSWER, IDLE
    }

    inner class Dashboard(private val player: Player) : Component<IProps, Dashboard.State>(IProps()) {
        inner class State {
            var status = Status.IDLE
        }

        init {
            this.state = State()
        }

        val onLeave = ButtonClick {event ->

            event.replyEmbeds(
                leave(player)
            ).queue()
        }

        override fun onRender(): Children {
            return {
                + on(started) {
                    when (state.status) {
                        Status.ASK -> AskPanel()
                        else -> Content("Loading")
                    }
                }

                + not(started) {
                    if (player.user == owner) ControlUI()
                    else waitingPanel()
                }
            }
        }
    }

    inner class AskPanel : Component<IProps, Unit>(IProps()) {
        override fun onRender(): Children {
            return {

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
