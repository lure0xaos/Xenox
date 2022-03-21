package gargoyle.xenox.game.scr.game.game

import java.awt.Point

class Ball(posX: Int, posY: Int, dX: Int, dY: Int) {
    val delta = Point()
    val position = Point()
    val newPosition = Point()
    var isNoX = false
    var isNoY = false

    init {
        position.setLocation(posX, posY)
        delta.setLocation(dX, dY)
    }
}
