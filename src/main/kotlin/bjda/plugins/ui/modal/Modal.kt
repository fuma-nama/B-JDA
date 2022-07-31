package bjda.plugins.ui.modal

import bjda.ui.component.row.Row
import bjda.utils.LambdaList
import bjda.utils.build
import net.dv8tion.jda.api.interactions.components.Modal

typealias ModalCreator = (id: String) -> Modal

fun modal(title: String, rows: LambdaList<Row>): ModalCreator {
    val child = rows.build().map {
        it.build()
    }

    return { id ->
        Modal.create(id, title)
            .addActionRows(child)
            .build()
    }
}

fun modal(title: String, vararg rows: Row): ModalCreator {
    val child = rows.map {
        it.build()
    }

    return { id ->
        Modal.create(id, title)
            .addActionRows(child)
            .build()
    }
}