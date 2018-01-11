package gargoyle.xenox.game.scr.status

import gargoyle.xenox.game.sys.JRunningScreen
import gargoyle.xenox.i.ScreenAssets
import gargoyle.xenox.util.applet.GApplet
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.net.URL

class JStatus(assets: ScreenAssets, private val applet: GApplet) : JRunningScreen(assets, SCR_STATUS, applet) {

    override fun draw(g: Graphics) {
        if (title.isNotEmpty()) {
            drawString(g, Pos.TOP, FONT_SMALL, title)
            drawString(g, Pos.CENTER, FONT_BIG, title)
            drawString(g, Pos.BOTTOM, FONT_SMALL, message)
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

    fun process(message: String, imageUrl: URL?) {
        clear()
        this.message = message
        if (imageUrl != null) {
            image = applet.getImage(imageUrl)
        }
        if (image != null) {
            val size = Dimension(image!!.getWidth(this), image!!.getHeight(this))
            preferredSize = size
            setSize(size)
        }
        process()
    }

    fun process(message: String, imageUrl: URL?, soundUrl: URL?, isMusic: Boolean) {
        val sound = options.isSound
        if (!sound) {
            musicStop()
        }
        if (soundUrl != null && sound) {
            val audioClip = (soundUrl)
            if (isMusic) {
                musicPlay(applet.getAudioClip(audioClip))
            } else {
                soundPlay(applet.getAudioClip(audioClip))
            }
        }
        process(message, imageUrl)
        musicStop()
    }

    internal enum class Pos {
        TOP, CENTER, BOTTOM
    }

    companion object {
        const val SCR_STATUS: String = "Status"
        const val STR_GAME_OVER: String = "game_over"
        const val STR_GET_READY: String = "get_ready"
        const val STR_LEVEL_FINISHED: String = "level_finished"
        const val STR_LIFE_LOST: String = "life_lost"
        private const val SIZE_BIG = 36
        private val FONT_BIG = Font(Font.MONOSPACED, Font.PLAIN, SIZE_BIG)
        private const val SIZE_SMALL = 18
        private val FONT_SMALL = Font(Font.MONOSPACED, Font.PLAIN, SIZE_SMALL)
    }
}
