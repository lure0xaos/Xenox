package gargoyle.xenox.game.scr.hi

import gargoyle.xenox.game.scr.menu.JIntro
import gargoyle.xenox.game.sys.HiScore
import gargoyle.xenox.game.sys.JRunningScreen
import gargoyle.xenox.i.ScreenAssets
import gargoyle.xenox.res.Res
import gargoyle.xenox.util.applet.GApplet
import java.awt.Color
import java.awt.Font
import java.awt.Graphics

class JHi(assets: ScreenAssets, applet: GApplet, private val hiScore: HiScore) :
    JRunningScreen(assets, SCR_HI, applet, applet.getImage(Res.url(JIntro.INTRO_IMAGE))) {

    init {
        background = BACKGROUND
        foreground = FOREGROUND
        font = FONT
    }

    override fun draw(g: Graphics) {
        val userName = hiScore.userName
        hiScore.records.forEachIndexed { i, record ->
            val color = if (record.name == userName) COLOR_ME else if (i % 3 == 0) FOREGROUND.darker() else FOREGROUND
            drawLine(g, i, record.name, record.score, color)
        }
    }

    private fun drawLine(g: Graphics, i: Int, name: String, score: Int, color: Color) {
        g.color = color
        val fm = getFontMetrics(font)
        val y = 5 + fm.ascent + 0.coerceAtLeast((height - fm.height * HiScore.LINES) / 2) + i * fm.height
        g.drawString(if (name.length > NAME_LIMIT) name.substring(0, NAME_LIMIT) else name, 5, y)
        val textScore = score.toString()
        g.drawString(textScore, width - fm.stringWidth(textScore) - 5, y)
    }

    companion object {
        const val SCR_HI: String = "Hi"
        private val BACKGROUND = Color.BLACK
        private val COLOR_ME = Color.BLUE
        private val FONT = Font(Font.SERIF, Font.BOLD, 36)
        private val FOREGROUND = Color.YELLOW
        private const val NAME_LIMIT = 10
    }
}
