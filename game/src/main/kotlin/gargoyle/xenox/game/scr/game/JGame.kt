package gargoyle.xenox.game.scr.game

import gargoyle.xenox.game.scr.game.game.Field
import gargoyle.xenox.game.scr.game.game.Item
import gargoyle.xenox.game.sys.JScreen
import gargoyle.xenox.i.ScreenAssets
import gargoyle.xenox.info.LevelInfo
import gargoyle.xenox.util.applet.GApplet
import java.awt.BorderLayout
import java.awt.Image

class JGame(assets: ScreenAssets, private val applet: GApplet) : JScreen(assets, SCR_GAME, applet) {
    private val field: Field = Field(applet)
    private val jField: JField
    private lateinit var level: LevelInfo

    private var running = false

    init {
        layout = BorderLayout()
        jField = JField(applet, this)
        add(jField, BorderLayout.CENTER)
        val stats = JStats(this)
        add(stats, BorderLayout.SOUTH)
    }

    override fun doProcess(): String? {
        field.resetBalls()
        musicLoop()
        running = true
        while (running) {
            when {
                field.percent > level.percent -> {
                    return id
                }

                !isPlayerAlive -> {
                    return null
                }

                field.hasGotItem(true, Item.ItemType.I_LEVEL) -> {
                    return id
                }

                else -> {
                    val l = System.currentTimeMillis()
                    if (isActive) {
                        if (field.gameStep(this) || field.hasGotItem(true, Item.ItemType.I_DEATH)) {
                            return null
                        }
                        val speed = (level.speed - System.currentTimeMillis() + l).coerceAtLeast(10).toInt()
                        repaint(speed.toLong())
                        try {
                            Thread.sleep(speed.toLong())
                        } catch (e: InterruptedException) {
                            return null
                        }
                    } else {
                        try {
                            Thread.sleep(1000)
                        } catch (e: InterruptedException) {
                            return null
                        }
                    }
                }
            }
        }
        return null
    }

    override fun destroy() {
        running = false
    }

    fun getField(): Field {
        return field
    }

    val playerScore: Int
        get() = this.field.player.score
    val isPlayerAlive: Boolean
        get() = this.field.player.isAlive

    fun loadLevel(levelNum: Int, level: LevelInfo) {
        this.level = level
        level.music?.let { musicSet(applet.getAudioClip(it)) }
        level.image?.let { setImage(applet.getImage(it)) }
        level.cover?.let { setCover(applet.getImage(it)) }
        size = preferredSize
        field.init(levelNum, level)
    }

    private fun setCover(cover: Image) {
        jField.setCover(cover)
    }

    private fun setImage(image: Image) {
        jField.setImage(image)
    }

    companion object {
        const val SCR_GAME: String = "Game"
    }
}
