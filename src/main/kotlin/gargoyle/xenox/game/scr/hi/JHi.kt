package gargoyle.xenox.game.scr.hi

import gargoyle.xenox.game.scr.menu.JIntro
import gargoyle.xenox.game.sys.Controls
import gargoyle.xenox.game.sys.HiScore
import gargoyle.xenox.game.sys.JRunningScreen
import gargoyle.xenox.game.sys.Options
import gargoyle.xenox.i.AudioManager
import gargoyle.xenox.i.ScreenManager
import gargoyle.xenox.util.i18n.Messages
import gargoyle.xenox.util.res.Resources
import java.awt.Color
import java.awt.Component
import java.awt.Font
import java.awt.Graphics
import java.awt.image.BufferedImage

class JHi(
    applet: Component,
    resources: Resources,
    messages: Messages,
    hiScore: HiScore,
    audio: AudioManager,
    screens: ScreenManager,
    options: Options,
    controls: Controls,
    title: String
) : JRunningScreen(
    SCR_HI, applet, resources.load(
        BufferedImage::class.java, JIntro.INTRO_IMAGE
    ), resources, messages, audio, screens, options, controls, title
) {
    private val hiScore: HiScore

    init {
        background = BACKGROUND
        foreground = FOREGROUND
        font = FONT
        this.hiScore = hiScore
    }

    override fun draw(g: Graphics) {
        val userName = hiScore.userName
        val records = hiScore.records
        for ((i, record) in records.withIndex()) {
            val color = if (record.name == userName) COLOR_ME else if (i % 3 == 0) FOREGROUND.darker() else FOREGROUND
            drawLine(g, i, record.name, record.score, color)
        }
    }

    private fun drawLine(g: Graphics, i: Int, name: String?, score: Int, color: Color) {
        val fm = getFontMetrics(font)
        val y0 = 5 + fm.ascent + 0.coerceAtLeast((height - fm.height * HiScore.LINES) / 2)
        g.color = color
        val y = y0 + i * fm.height
        g.drawString(if (name!!.length > NAME_LIMIT) name.substring(0, NAME_LIMIT) else name, 5, y)
        val textScore = score.toString()
        g.drawString(textScore, width - fm.stringWidth(textScore) - 5, y)
    }

    companion object {
        const val SCR_HI = "Hi"
        private val BACKGROUND = Color.BLACK
        private val COLOR_ME = Color.BLUE
        private val FONT = Font(Font.SERIF, Font.BOLD, 36)
        private val FOREGROUND = Color.YELLOW
        private const val NAME_LIMIT = 10
    }
}
