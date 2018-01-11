package gargoyle.xenox.game.scr.game.game

import java.awt.Point

class Ball(posX: Int, posY: Int, dX: Int, dY: Int) {
    val delta: Point = Point()
    val position: Point = Point()
    val newPosition: Point = Point()
    var isNoX: Boolean = false
    var isNoY: Boolean = false

    init {
        position.setLocation(posX, posY)
        delta.setLocation(dX, dY)
    }
}
