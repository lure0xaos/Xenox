package gargoyle.xenox.game.scr.game

import gargoyle.xenox.Xenox
import gargoyle.xenox.game.scr.game.game.Ball
import gargoyle.xenox.game.scr.game.game.Field
import gargoyle.xenox.game.scr.game.game.Item
import gargoyle.xenox.res.Res
import gargoyle.xenox.util.applet.GApplet
import java.awt.*
import javax.swing.JComponent

internal class JField(private val app: GApplet, private val game: JGame) : JComponent() {

    private var cover: Image? = null

    private var image: Image? = null

    init {
        if (!init) {
            imgFreeBall = app.getImage(Res.url("freeball.gif"))
            imgWallBall = app.getImage(Res.url("wallball.gif"))
            imgPlayer = app.getImage(Res.url("player.gif"))
            imgItemBallMinus = app.getImage(Res.url("itemballminus.gif"))
            imgItemBallPlus = app.getImage(Res.url("itemballplus.gif"))
            imgItemLife = app.getImage(Res.url("itemlife.gif"))
            imgItemLevel = app.getImage(Res.url("itemlevel.gif"))
            imgItemScore = app.getImage(Res.url("itemscore.gif"))
            imgItemDeath = app.getImage(Res.url("itemdeath.gif"))
            init = true
        }
        background = Color.BLACK
        foreground = Color.WHITE
        font = Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE)
    }


    private fun drawBall(g: Graphics, ball: Ball) {
        val position = ball.position
        drawImage(
            g, getRect(position),
            if (game.getField().`is`(position.x, position.y, Field.C_FREE)) imgFreeBall else imgWallBall
        )
    }

    private fun drawBalls(g: Graphics) {
        for (ball in game.getField().getBalls()) {
            drawBall(g, ball)
        }
    }

    private fun drawEmpty(g: Graphics, rectangle: Rectangle) {
        val c = g.color
        g.color = Color.BLACK
        when {
            cover != null -> drawImageCell(g, cover, rectangle)
            else -> g.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height)
        }
        g.color = c
    }

    private fun drawImage(g: Graphics, rectangle: Rectangle, img: Image) {
        g.drawImage(
            img, rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height, 0, 0,
            img.getWidth(this), img.getHeight(this), this
        )
    }

    private fun drawImageCell(g: Graphics, img: Image?, rectangle: Rectangle) {
        if (img != null) {
            val dw = img.getWidth(this) / width.toDouble()
            val dh = img.getHeight(this) / height.toDouble()
            g.drawImage(
                img,
                rectangle.x,
                rectangle.y,
                rectangle.x + rectangle.width,
                rectangle.y + rectangle.height,
                (rectangle.x * dw).toInt(),
                (rectangle.y * dh).toInt(),
                ((rectangle.x + rectangle.width) * dw).toInt(),
                ((rectangle.y + rectangle.height) * dh).toInt(),
                this
            )
        }
    }

    private fun drawItem(g: Graphics) {
        val field = game.getField()
        val item = field.item
        if (item != null) {
            val c = g.color
            val position = item.position
            val rectangle = getRect(position)
            if (field.`is`(position.x, position.y, Field.C_PATH)) {
                drawEmpty(g, rectangle)
            }
            if (field.`is`(position.x, position.y, Field.C_WALL)) {
                drawWall(g, rectangle)
            }
            when (item.type) {
                Item.ItemType.I_MINUS_BALL -> drawImage(g, rectangle, imgItemBallMinus)
                Item.ItemType.I_PLUS_BALL -> drawImage(g, rectangle, imgItemBallPlus)
                Item.ItemType.I_LIFE -> drawImage(g, rectangle, imgItemLife)
                Item.ItemType.I_LEVEL -> drawImage(g, rectangle, imgItemLevel)
                Item.ItemType.I_SCORE -> drawImage(g, rectangle, imgItemScore)
                Item.ItemType.I_DEATH -> drawImage(g, rectangle, imgItemDeath)
            }
            g.color = c
        }
    }

    private fun drawPath(g: Graphics, rectangle: Rectangle) {
        val c = g.color
        when {
            cover != null -> {
                g.color = COLOR_PATH
                drawImageCell(g, cover, rectangle)
            }

            else -> g.color = Color.RED
        }
        g.fill3DRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, true)
        g.color = c
    }

    private fun drawPlayer(g: Graphics) {
        val c = g.color
        val field = game.getField()
        if (!field.isInitialized()) return
        val position = field.player.position
        val rectangle = getRect(position)
        if (field.`is`(position.x, position.y, Field.C_PATH)) {
            drawEmpty(g, rectangle)
        }
        if (field.`is`(position.x, position.y, Field.C_WALL)) {
            drawWall(g, rectangle)
        }
        drawImage(g, rectangle, imgPlayer)
        g.color = c
    }

    private fun drawWall(g: Graphics, rectangle: Rectangle) {
        val c = g.color
        g.color = Color.BLUE
        if (image != null) {
            drawImageCell(g, image, rectangle)
        }
        g.draw3DRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, true)
        g.color = c
    }

    private fun getRect(position: Point): Rectangle {
        val r = Rectangle()
        getRect(r, position)
        return r
    }

    private fun getRect(r: Rectangle, position: Point) {
        val cellSizeX = width / game.getField().width.toDouble()
        val cellSizeY = height / game.getField().height.toDouble()
        r.setBounds(
            (-ROUND + position.x * cellSizeX).toInt(),
            (-ROUND + position.y * cellSizeY).toInt(),
            (ROUND + cellSizeX).toInt(),
            (ROUND + cellSizeY).toInt()
        )
    }

    override fun getPreferredSize(): Dimension {
        return if (image == null) Dimension(
            game.getField().width * Xenox.PF_CELL,
            game.getField().height * Xenox.PF_CELL
        ) else Dimension(image!!.getWidth(this), image!!.getHeight(this))
    }

    override fun paint(g: Graphics) {
        if (width == 0 || height == 0) {
            return
        }
        val field = game.getField()
        if (field.width != 0 && field.height != 0) {
            val position = Point()
            val rectangle = Rectangle()
            position.x = 0
            while (position.x < field.width) {
                position.y = 0
                while (position.y < field.height) {
                    getRect(rectangle, position)
                    when (field[position.x, position.y]) {
                        Field.C_FREE -> drawEmpty(g, rectangle)
                        Field.C_WALL -> drawWall(g, rectangle)
                        Field.C_PATH -> drawPath(g, rectangle)
                        else -> {}
                    }
                    position.y++
                }
                position.x++
            }
            drawPlayer(g)
            drawBalls(g)
            drawItem(g)
        } else when {
            cover != null -> g.drawImage(
                cover,
                0,
                0,
                width,
                height,
                0,
                0,
                cover!!.getWidth(this),
                cover!!.getHeight(this),
                this
            )

            else -> super.paint(g)
        }
    }

    fun setCover(cover: Image) {
        this.cover = (cover)
    }

    fun setImage(image: Image) {
        this.image = (image)
    }

    companion object {
        private val COLOR = Color(255, 0, 0, 128)
        private val COLOR_PATH = COLOR
        private const val FONT_SIZE = 12
        private const val ROUND = 1.5

        private lateinit var imgFreeBall: Image

        private lateinit var imgItemBallMinus: Image

        private lateinit var imgItemBallPlus: Image

        private lateinit var imgItemDeath: Image

        private lateinit var imgItemLevel: Image

        private lateinit var imgItemLife: Image

        private lateinit var imgItemScore: Image

        private lateinit var imgPlayer: Image

        private lateinit var imgWallBall: Image

        private var init = false
    }
}
