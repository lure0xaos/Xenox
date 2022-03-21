package gargoyle.xenox.game.scr.game

import gargoyle.xenox.game.scr.game.game.Field
import gargoyle.xenox.game.scr.game.game.Item
import gargoyle.xenox.game.scr.game.game.Level
import gargoyle.xenox.game.sys.Controls
import gargoyle.xenox.game.sys.JScreen
import gargoyle.xenox.game.sys.Options
import gargoyle.xenox.i.AudioManager
import gargoyle.xenox.i.ScreenManager
import gargoyle.xenox.util.i18n.Messages
import gargoyle.xenox.util.res.Resources
import java.awt.BorderLayout
import java.awt.Component
import java.net.URL

class JGame(
    applet: Component,
    resources: Resources,
    messages: Messages,
    controls: Controls,
    audio: AudioManager,
    screens: ScreenManager,
    options: Options
) : JScreen(
    SCR_GAME, applet, resources, messages, controls, audio, screens, options
) {
    private val field: Field
    private val jfield: JField
    private var level: Level? = null

    private var running = false

    init {
        field = Field(resources)
        layout = BorderLayout()
        jfield = JField(this, resources)
        add(jfield, BorderLayout.CENTER)
        val stats = JStats(this)
        add(stats, BorderLayout.SOUTH)
    }

    override fun _process(): String? {
        field.resetBalls()
        musicLoop()
        running = true
        while (running) {
            if (field.percent > level!!.percent) {
                return id
            }
            if (!isPlayerAlive) {
                return null
            }
            if (field.hasGotItem(true, Item.Companion.I_LEVEL)) {
                return id
            }
            val l = System.currentTimeMillis()
            if (isActive) {
                if (field.gameStep(this) || field.hasGotItem(true, Item.Companion.I_DEATH)) {
                    return null
                }
                val speed = Math.max(10, level!!.speed - System.currentTimeMillis() + l).toInt()
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
        return null
    }

    override fun destroy() {
        running = false
    }

    fun getField(): Field {
        return field
    }

    val playerScore: Int
        get() = this.field.player!!.score
    val isPlayerAlive: Boolean
        get() = this.field.player!!.isAlive

    fun loadLevel(levelNum: Int, level: Level) {
        this.level = level
        musicSet(level.music!!)
        setImage(level.image)
        setCover(level.cover)
        size = preferredSize
        field.init(levelNum, level)
    }

    private fun setCover(cover: URL?) {
        jfield.setCover(cover)
    }

    private fun setImage(image: URL?) {
        jfield.setImage(image)
    }

    companion object {
        const val SCR_GAME = "Game"
    }
}
