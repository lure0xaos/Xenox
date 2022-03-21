package gargoyle.xenox.game.scr.game.game

import java.awt.Point
import java.io.Serializable

class Player(var lives: Int) : Serializable {
    val last = Point()
    val position = Point()
    var score = 0

    val isAlive: Boolean
        get() = lives > 0

    companion object {
        private const val serialVersionUID = 2800736031194151590L
    }
}
