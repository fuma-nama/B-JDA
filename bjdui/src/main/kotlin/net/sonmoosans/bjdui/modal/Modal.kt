package net.sonmoosans.bjdui.modal

import net.sonmoosans.bjdui.component.row.Row
import net.sonmoosans.bjda.utils.LambdaList
import net.sonmoosans.bjda.utils.build
import net.dv8tion.jda.api.interactions.components.Modal

fun modal(title: String, rows: LambdaList<Row>): ModalCreator {
    val child = rows.build().map {
        it.build()
    }

    return ModalCreator { id ->
        Modal.create(id, title)
            .addActionRows(child)
            .build()
    }
}

fun modal(title: String, vararg rows: Row): ModalCreator {
    val child = rows.map {
        it.build()
    }

    return ModalCreator { id ->
        Modal.create(id, title)
            .addActionRows(child)
            .build()
    }
}

fun interface ModalCreator {
    fun create(id: String): Modal

    operator fun invoke(id: String) = create(id)
}