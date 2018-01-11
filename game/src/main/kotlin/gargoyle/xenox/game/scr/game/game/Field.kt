package gargoyle.xenox.game.scr.game.game

import gargoyle.xenox.Xenox
import gargoyle.xenox.game.scr.game.JGame
import gargoyle.xenox.info.LevelInfo
import gargoyle.xenox.res.Res
import gargoyle.xenox.util.applet.Applet
import java.awt.Point
import java.util.*

class Field(private val app: Applet) {
    private val balls: MutableList<Ball> = mutableListOf()
    private lateinit var f: Array<ByteArray>
    private var gotItem: Item? = null
    var height: Int = 0
        private set
    var item: Item? = null
        private set
    lateinit var level: LevelInfo
        private set
    var levelNum: Int = 0
        private set
    var percent: Int = 0
        private set
    lateinit var player: Player
        private set
    var width: Int = 0
        private set

    private fun cancelPath() {
        val position = player.position
        position.location = player.last
        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                if (`is`(x, y, C_PATH)) {
                    set(x, y, C_FREE)
                }
            }
        }
    }

    fun gameStep(game: JGame): Boolean {
        val controls = game.controls
        val position = player.position
        val old = Point(position)
        if (`is`(position.x, position.y, C_PATH) && controls.isAction) {
            cancelPath()
        } else if (controls.isUp) {
            if (position.y > 0) {
                position.y--
            }
        } else if (controls.isDown) {
            if (position.y < height - 1) {
                position.y++
            }
        } else if (controls.isLeft) {
            if (position.x > 0) {
                position.x--
            }
        } else if (controls.isRight) {
            if (position.x < width - 1) {
                position.x++
            }
        }
        if (`is`(position.x, position.y, C_PATH)) {
            position.location = old
        }
        var lostLife = false
        for (ball in balls) {
            if (ball.position == position) {
                looseLife()
                lostLife = true
                break
            }
        }
        if (!lostLife) {
            if (`is`(position.x, position.y, C_WALL)) {
                player.last.location = position
            }
            if (`is`(position.x, position.y, C_FREE)) {
                set(position.x, position.y, C_PATH)
            }
            var got = false
            if (item != null && item!!.position == player.position) {
                gotItem = item
                item = null
                if (gotItem(
                        Item.ItemType.I_MINUS_BALL,
                        Item.ItemType.I_PLUS_BALL,
                        Item.ItemType.I_SCORE,
                        Item.ItemType.I_LIFE
                    )
                ) {
                    got = true
                }
            }
            if (`is`(position.x, position.y, C_WALL) && `is`(old.x, old.y, C_PATH)) {
                got = lineFinished()
            }
            if (got) {
                game.soundPlay(app.getAudioClip(Res.url(Xenox.SND_GOT)))
            }
        }
        val newPos = Point()
        val position1 = Point()
        for (ball in balls) {
            position1.location = ball.position
            val free = get(position1.x, position1.y)
            val delta = ball.delta
            newPos.setLocation(position1.x + delta.x, position1.y + delta.y)
            var newPos2: Point? = null
            for (ball2 in balls) {
                val position2 = ball2.position
                val delta2 = ball2.delta
                newPos2 = Point(position2.x + delta2.x, position2.y + delta2.y)
                if (ball === ball2 || newPos != newPos2) {
                    newPos2 = null
                }
            }
            ball.isNoX = false
            ball.isNoY = false
            if (!isIn(newPos.x, newPos.y) || !`is`(newPos.x, newPos.y, free) || newPos2 != null) {
                if (isIn(newPos.x, position1.y) && `is`(newPos.x, position1.y, free) && isIn(
                        position1.x,
                        newPos.y
                    ) && `is`(position1.x, newPos.y, free) || newPos2 != null &&
                    (newPos.x != newPos2.x || position1.y != newPos2.y) &&
                    (position1.x != newPos2.x || newPos.y != newPos2.y)
                ) {
                    delta.x = -delta.x
                    delta.y = -delta.y
                    ball.isNoX = true
                    ball.isNoY = true
                } else {
                    if (!isIn(newPos.x, position1.y) || !`is`(
                            newPos.x,
                            position1.y,
                            free
                        ) || newPos2 != null && newPos.x == newPos2.x && position1.y == newPos2.y
                    ) {
                        delta.x = -delta.x
                        ball.isNoX = true
                    }
                    if (free == C_FREE && `is`(newPos.x, position1.y, C_PATH)) {
                        looseLife()
                        lostLife = true
                    } else {
                        if (!isIn(position1.x, newPos.y) || !`is`(
                                position1.x,
                                newPos.y,
                                free
                            ) || newPos2 != null && position1.x == newPos2.x && newPos.y == newPos2.y
                        ) {
                            delta.y = -delta.y
                            ball.isNoY = true
                        }
                        if (free == C_FREE && `is`(position1.x, newPos.y, C_PATH)) {
                            looseLife()
                            lostLife = true
                        }
                    }
                }
                if (free == C_FREE && `is`(newPos.x, newPos.y, C_PATH)) {
                    looseLife()
                    lostLife = true
                }
                if (ball.isNoX && !ball.isNoY) {
                    position1.y += delta.y
                }
                if (ball.isNoY && !ball.isNoX) {
                    position1.x += delta.x
                }
                ball.newPosition.location = position1
            } else {
                position1.location = newPos
                ball.newPosition.location = newPos
            }
        }
        for (ball in balls) {
            ball.position.location = ball.newPosition
        }
        if (!lostLife) {
            if (item == null) {
                if (rnd.nextInt(Xenox.ITEM_SHOW) == 0) {
                    item = Item()
                    val pos = Point()
                    do {
                        pos.x = rnd.nextInt(width)
                        pos.y = rnd.nextInt(height)
                    } while (!`is`(pos.x, pos.y, C_FREE))
                    item!!.position = (pos)
                }
            } else {
                if (rnd.nextInt(Xenox.ITEM_HIDE) == 0) {
                    item = null
                }
            }
        }
        return lostLife
    }

    operator fun get(x: Int, y: Int): Byte {
        return if (isIn(x, y)) f[x][y] else C_WALL
    }

    fun getBalls(): Collection<Ball> {
        return Collections.unmodifiableList(balls)
    }

    private fun gotItem(vararg types: Item.ItemType): Boolean {
        if (gotItem != null) {
            for (type in types) {
                if (type == gotItem!!.type) {
                    when (type) {
                        Item.ItemType.I_LIFE -> player.lives = (player.lives + 1)
                        Item.ItemType.I_SCORE -> player.score = (player.score + 100)
                        Item.ItemType.I_MINUS_BALL -> balls.removeAt(
                            rnd.nextInt(
                                balls.size
                            )
                        )

                        Item.ItemType.I_PLUS_BALL -> {
                            val pos = Point()
                            if (rnd.nextBoolean()) {
                                do {
                                    pos.setLocation(
                                        rnd.nextInt(width - 2) + 1,
                                        rnd.nextInt(height - 2) + 1
                                    )
                                } while (get(pos.x, pos.y) != C_FREE)
                                val ball = Ball(
                                    pos.x, pos.y, if (rnd.nextInt() and 1 == 0) 1 else -1,
                                    if (rnd.nextInt() and 1 == 0) 1 else -1
                                )
                                balls.add(ball)
                            } else {
                                do {
                                    pos.setLocation(rnd.nextInt(width - 2) + 1, height - 1)
                                } while (get(pos.x, pos.y) != C_WALL)
                                val ball = Ball(
                                    pos.x, pos.y, if (rnd.nextInt() and 1 == 0) 1 else -1,
                                    if (rnd.nextInt() and 1 == 0) 1 else -1
                                )
                                balls.add(ball)
                            }
                        }

                        else -> {}
                    }
                    gotItem = null
                    item = null
                    return true
                }
            }
        }
        return false
    }

    fun hasGotItem(last: Boolean, vararg types: Item.ItemType): Boolean {
        if (gotItem != null) {
            for (type in types) {
                if (type == gotItem!!.type) {
                    if (last) {
                        gotItem = null
                        item = null
                    }
                    return true
                }
            }
        }
        return false
    }

    private fun init() {
        val width = level.width
        val height = level.height
        this.width = width
        this.height = height
        f = Array(width) { ByteArray(height) }
        balls.clear()
        balls += ArrayList(level.balls)
        player = Player(level.lives)
        percent = 0
        for (x in 0 until this.width) {
            for (y in 0 until this.height) {
                if (x == 0 || y == 0 || x == this.width - 1 || y == this.height - 1) {
                    set(x, y, C_WALL)
                } else {
                    set(x, y, C_FREE)
                }
            }
        }
        reset()
    }

    fun init(levelNum: Int, level: LevelInfo) {
        this.levelNum = levelNum
        this.level = level
        init()
    }

    private fun initPlayerLocation() {
        player.position.setLocation((width - 2) / 2, 0)
    }

    fun `is`(x: Int, y: Int, c: Byte): Boolean {
        return if (isIn(x, y)) f[x][y] == c else c == C_WALL
    }

    private fun isIn(x: Int, y: Int): Boolean {
        return x >= 0 && y >= 0 && x < width && y < height
    }

    private fun lineFinished(): Boolean {
        val stk = Stack<Point>()
        for (ball in balls) {
            val position = ball.position
            if (C_FREE != get(position.x, position.y)) {
                continue
            }
            set(position.x, position.y, C_NO)
            stk.push(Point(position))
            while (!stk.isEmpty()) {
                val p = stk.pop()
                if (`is`(p.x - 1, p.y, C_FREE)) {
                    set(p.x - 1, p.y, C_NO)
                    stk.push(Point(p.x - 1, p.y))
                }
                if (`is`(p.x + 1, p.y, C_FREE)) {
                    set(p.x + 1, p.y, C_NO)
                    stk.push(Point(p.x + 1, p.y))
                }
                if (`is`(p.x, p.y - 1, C_FREE)) {
                    set(p.x, p.y - 1, C_NO)
                    stk.push(Point(p.x, p.y - 1))
                }
                if (`is`(p.x, p.y + 1, C_FREE)) {
                    set(p.x, p.y + 1, C_NO)
                    stk.push(Point(p.x, p.y + 1))
                }
            }
        }
        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                if (`is`(x, y, C_FREE) || `is`(x, y, C_PATH)) {
                    player.score = (player.score + 1)
                    set(x, y, C_WALL)
                }
            }
        }
        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                if (`is`(x, y, C_NO)) {
                    set(x, y, C_FREE)
                }
            }
        }
        var occ = 0
        var ret = false
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (`is`(x, y, C_WALL)) {
                    occ++
                    if (item != null) {
                        val pos = item!!.position
                        if (pos.x == x && pos.y == y) {
                            gotItem = item
                            item = null
                            if (gotItem(
                                    Item.ItemType.I_MINUS_BALL,
                                    Item.ItemType.I_PLUS_BALL,
                                    Item.ItemType.I_SCORE,
                                    Item.ItemType.I_LIFE
                                )
                            ) {
                                ret = true
                            }
                        }
                    }
                }
            }
        }
        percent = (occ * 100 / (width * height).toDouble()).toInt()
        return ret
    }

    private fun looseLife() {
        val position = player.position
        if (`is`(position.x, position.y, C_WALL)) {
            initPlayerLocation()
        } else {
            position.location = player.last
        }
        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                if (`is`(x, y, C_PATH)) {
                    set(x, y, C_FREE)
                }
            }
        }
        player.lives--
    }

    private fun reset() {
        initPlayerLocation()
        setBalls()
        gotItem = null
        item = null
    }

    fun resetBalls() {
        val pos = Point()
        for (ball in balls) {
            val position = ball.position
            if (`is`(position.x, position.y, C_WALL)) {
                do {
                    position.setLocation(rnd.nextInt(width - 2) + 1, height - 1)
                } while (get(pos.x, pos.y) != C_WALL)
            }
        }
        for (ball in balls) {
            ball.delta
                .setLocation(if (rnd.nextInt() and 1 == 0) 1 else -1, if (rnd.nextInt() and 1 == 0) 1 else -1)
        }
    }

    private operator fun set(x: Int, y: Int, c: Byte) {
        if (isIn(x, y)) {
            f[x][y] = c
        }
    }

    private fun setBalls() {
        balls.clear()
        val b2 = level.balls / 2
        val b1 = level.balls - b2
        val pos = Point()
        for (b in 0 until b1) {
            do {
                pos.setLocation(rnd.nextInt(width - 2) + 1, rnd.nextInt(height - 2) + 1)
            } while (get(pos.x, pos.y) != C_FREE)
            val ball = Ball(
                pos.x, pos.y, if (rnd.nextInt() and 1 == 0) 1 else -1,
                if (rnd.nextInt() and 1 == 0) 1 else -1
            )
            balls.add(ball)
        }
        for (b in 0 until b2) {
            do {
                pos.setLocation(rnd.nextInt(width - 2) + 1, height - 1)
            } while (get(pos.x, pos.y) != C_WALL)
            val ball = Ball(
                pos.x, pos.y, if (rnd.nextInt() and 1 == 0) 1 else -1,
                if (rnd.nextInt() and 1 == 0) 1 else -1
            )
            balls.add(ball)
        }
    }

    fun isInitialized(): Boolean =
        this::player.isInitialized

    companion object {
        const val C_FREE: Byte = 0
        const val C_PATH: Byte = 2
        const val C_WALL: Byte = 1
        private const val C_NO: Byte = 3
        private val rnd = Random()
    }
}
