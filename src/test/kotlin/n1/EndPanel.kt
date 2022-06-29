package n1

import bjda.ui.component.Embed
import bjda.ui.core.Component.Companion.rangeTo
import bjda.ui.core.FComponent
import bjda.ui.core.IProps
import java.awt.Color

class EndPanelProps : IProps() {
    lateinit var player: Player
    var isWinner: Boolean = false
    lateinit var winners: List<Player>
}
val EndPanel = FComponent.noState(::EndPanelProps) {
    val winnersText = props.winners.map { it.user.name + ", " };

    {
        with (props) {

            + Embed()..{
                if (isWinner) {
                    title = "You are the Winner!"
                    color = Color.GREEN
                } else {
                    title = "You lost..."
                    color = Color.RED
                }

                description = "Winners: $winnersText"

                fields = fields(
                    field("Score" to player.score.toString()),
                )

                footer = "Note: This game support multi winners"
            }
        }
    }
}