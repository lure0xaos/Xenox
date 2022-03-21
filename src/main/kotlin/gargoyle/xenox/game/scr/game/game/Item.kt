package gargoyle.xenox.game.scr.game.game

import java.awt.Point

class Item {
    var position = Point()
        get() = field.location
        set(position) {
            field.location = position
        }
    val type: Byte = (1 + Math.random() * 6).toInt().toByte()

    companion object {
        const val I_DEATH: Byte = 6
        const val I_LEVEL: Byte = 5
        const val I_LIFE: Byte = 4
        const val I_MINUS_BALL: Byte = 2
        const val I_PLUS_BALL: Byte = 1
        const val I_SCORE: Byte = 3
    }
}
