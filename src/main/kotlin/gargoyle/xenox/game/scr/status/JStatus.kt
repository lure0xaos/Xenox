package gargoyle.xenox.game.scr.status

import gargoyle.xenox.game.sys.Controls
import gargoyle.xenox.game.sys.JRunningScreen
import gargoyle.xenox.game.sys.Options
import gargoyle.xenox.i.AudioManager
import gargoyle.xenox.i.ScreenManager
import gargoyle.xenox.util.i18n.Messages
import gargoyle.xenox.util.res.Resources
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.net.URL

class JStatus(
    applet: Component,
    resources: Resources?,
    messages: Messages,
    audio: AudioManager,
    screens: ScreenManager,
    options: Options,
    controls: Controls,
    title: String
) : JRunningScreen(
    SCR_STATUS, applet, resources, messages, audio, screens, options, controls, title
) {
    override fun draw(g: Graphics) {
        if (!isEmpty(title)) drawString(g, Pos.TOP, FONT_SMALL, title!!)
        if (!isEmpty(title)) {
            drawString(g, Pos.CENTER, FONT_BIG, title!!)
        }
        if (!isEmpty(message)) {
            drawString(g, Pos.BOTTOM, FONT_SMALL, message!!)
        }
    }

    private fun drawString(g: Graphics, pos: Pos, f: Font, str: String) {
        val fm = g.getFontMetrics(f)
        val bb = fm.getStringBounds(str, 0, str.length, g).bounds
        g.color = Color.BLACK
        val y: Int = when (pos) {
            Pos.TOP -> 0
            Pos.CENTER -> (height - bb.height) / 2
            Pos.BOTTOM -> height - bb.height
        }
        g.fillRect(0, y, width, bb.height)
        g.color = Color.WHITE
        g.font = f
        g.drawString(str, (width - bb.width) / 2, y + fm.ascent)
    }

    fun process(title: String?, message: String?, imageUrl: URL?) {
        clear()
        //        applet.showStatus(message);
        this.title = title
        this.message = message
        if (imageUrl != null) {
            image = resources!!.load(BufferedImage::class.java, imageUrl)
        }
        if (image != null) {
            val size = Dimension(image!!.getWidth(this), image!!.getHeight(this))
            preferredSize = size
            setSize(size)
        }
        process()
    }

    fun process(title: String?, message: String?, imageUrl: URL?, soundUrl: URL?) {
        val sound = options.isSound
        if (!sound) {
            musicStop()
        }
        if (soundUrl != null && sound) {
            soundPlay(soundUrl)
        }
        process(title, message, imageUrl)
        musicStop()
    }

    internal enum class Pos {
        TOP, CENTER, BOTTOM
    }

    companion object {
        const val SCR_STATUS = "Status"
        const val STR_GAME_OVER = "game_over"
        const val STR_GET_READY = "get_ready"
        const val STR_LEVEL_FINISHED = "level_finished"
        const val STR_LIFE_LOST = "life_lost"
        private const val SIZE_BIG = 36
        private val FONT_BIG = Font(Font.MONOSPACED, Font.PLAIN, SIZE_BIG)
        private const val SIZE_SMALL = 18
        private val FONT_SMALL = Font(Font.MONOSPACED, Font.PLAIN, SIZE_SMALL)
        private fun isEmpty(s: String?): Boolean {
            return s == null || s.isEmpty()
        }
    }
}
