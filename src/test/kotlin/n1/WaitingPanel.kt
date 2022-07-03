package n1

import bjda.plugins.ui.hook.ButtonClick
import bjda.ui.component.Embed
import bjda.ui.component.Row
import bjda.ui.component.action.Button
import bjda.ui.core.Component
import bjda.ui.core.Component.Companion.minus
import bjda.ui.core.Component.Companion.rangeTo
import bjda.ui.core.FComponent
import bjda.ui.core.IProps
import bjda.ui.types.Children
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle

fun LoadingPanel(): Embed {
    return Embed()..{
        title = "Loading..."
        thumbnail = "https://upload.wikimedia.org/wikipedia/commons/a/ad/YouTube_loading_symbol_3_%28transparent%29.gif"
    }
}

class WaitingPanel : Component<WaitingPanel.Props>(Props()) {
    class Props : IProps() {
        lateinit var game: VoteGame
        lateinit var player: Player
    }

    private val onStart = ButtonClick { event ->
        ui.switchTo(LoadingPanel(), false)

        ui.edit(event) {
            props.game.start()
        }
    }

    private val onLeave = ButtonClick { event ->
        with(props) {
            event.replyEmbeds(
                game.leave(player)
            ).queue()
        }
    }

    override fun onRender(): Children {
        val game = props.game

        return {
            val root = this@WaitingPanel

            + if (props.player.isOwner) {
                ControlUI()..{
                    id = game.id
                    onStart = root.onStart
                    players = game.players
                }
            } else {
                MemberUI {
                    onLeave = root.onLeave
                }
            }
        }
    }
}

class MemberUIProps : IProps() {
    lateinit var onLeave: ButtonClick
}
val MemberUI = FComponent.create(::MemberUIProps) {
    {
        + Embed()..{
            title = "Joined the Game"
            description = "Waiting to start..."
        }

        + Row() -{
            + Button {
                id = use(props.onLeave)
                label = "Leave"
                style = ButtonStyle.DANGER
            }
        }
    }
}

class ControlUI : Component<ControlUI.Props>(::Props) {
    class Props: IProps() {
        lateinit var id: String
        lateinit var onStart: ButtonClick
        lateinit var players: List<Player>
    }

    override fun onRender(): Children {
        return with (props) {{
            + Embed()..{
                title = "Waiting Players..."
                description = "${players.size} Players joined (Channel Id: `$id`)"
                fields = players.map {player ->
                    MessageEmbed.Field(player.user.name, "", false)
                }
            }

            + Row()-{
                + Button(id = use(onStart)) {
                    label = "Start Game"
                    disabled = players.size < 2
                }
            }
        }}
    }
}