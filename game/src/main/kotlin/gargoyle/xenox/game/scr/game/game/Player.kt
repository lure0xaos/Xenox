package gargoyle.xenox.game.scr.game.game

import java.awt.Point

class Player(var lives: Int) {
    val last: Point = Point()
    val position: Point = Point()
    var score: Int = 0

    val isAlive: Boolean
        get() = lives > 0

}
