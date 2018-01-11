package gargoyle.xenox.game.scr.game.game

import java.awt.Point

class Item {
    var position: Point = Point()
        get() = field.location
        set(position) {
            field.location = position
        }
    val type: ItemType = ItemType.values()[(Math.random() * ItemType.values().size).toInt()]

    enum class ItemType {
        I_DEATH,
        I_LEVEL,
        I_LIFE,
        I_MINUS_BALL,
        I_PLUS_BALL,
        I_SCORE,
    }
}
