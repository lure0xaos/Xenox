package gargoyle.xenox.game.scr.game

import gargoyle.xenox.util.i18n.get
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

internal class JStats(private val game: JGame) : JPanel() {
    private val balls: JLabel
    private val level: JLabel
    private val lives: JLabel
    private val needed: JLabel
    private val percent: JLabel
    private val score: JLabel

    init {
        layout = GridLayout(0, 6)
        background = Color.BLACK
        createLabelSmall(STR_STAT_SCORE).also { add(it) }
        score = createLabelBig().also { add(it) }
        createLabelSmall(STR_STAT_LEVEL).also { add(it) }
        level = createLabelBig().also { add(it) }
        createLabelSmall(STR_STAT_LIVES).also { add(it) }
        lives = createLabelBig().also { add(it) }
        createLabelSmall(STR_STAT_PERCENTS).also { add(it) }
        percent = createLabelBig().also { add(it) }
        createLabelSmall(STR_STAT_NEEDED).also { add(it) }
        needed = createLabelBig().also { add(it) }
        createLabelSmall(STR_STAT_BALLS).also { add(it) }
        balls = createLabelBig().also { add(it) }
        isOpaque = true
    }

    private fun createLabelSmall(str: String): JLabel =
        JLabel("${game.messages[str]}: ").apply {
            horizontalAlignment = SwingConstants.LEFT
            font = Font(Font.MONOSPACED, Font.PLAIN, SIZE_SMALL)
            background = Color.BLACK
            foreground = Color.LIGHT_GRAY
            isOpaque = true
        }

    private fun createLabelBig(): JLabel =
        JLabel("").apply {
            horizontalAlignment = SwingConstants.LEFT
            font = Font(Font.MONOSPACED, Font.PLAIN, SIZE_BIG)
            background = Color.BLACK
            foreground = Color.WHITE
            isOpaque = true
        }

    override fun paint(g: Graphics) {
        val field = game.getField()
        if (field.isInitialized()) {
            val player = field.player
            score.text = player.score.toString()
            level.text = field.levelNum.toString()
            lives.text = player.lives.toString()
            percent.text = field.percent.toString()
            needed.text = field.level.percent.toString()
            balls.text = field.getBalls().size.toString()
        }
        super.paint(g)
    }

    companion object {
        private const val SIZE_BIG = 15
        private const val SIZE_SMALL = 8
        private const val STR_STAT_BALLS = "balls"
        private const val STR_STAT_LEVEL = "level"
        private const val STR_STAT_LIVES = "lives"
        private const val STR_STAT_NEEDED = "target"
        private const val STR_STAT_PERCENTS = "open"
        private const val STR_STAT_SCORE = "score"
    }
}
