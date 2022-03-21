package gargoyle.xenox.game.scr.game

import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

internal class JStats(private val game: JGame) : JPanel() {
    private var balls: JLabel? = null
    private var level: JLabel? = null
    private var lives: JLabel? = null
    private var needed: JLabel? = null
    private var percent: JLabel? = null
    private var score: JLabel? = null

    init {
        layout = GridLayout(0, 6)
        background = Color.BLACK
        add(createLabel(STR_STAT_SCORE))
        add(createLabel2().also { score = it })
        add(createLabel(STR_STAT_LEVEL))
        add(createLabel2().also { level = it })
        add(createLabel(STR_STAT_LIVES))
        add(createLabel2().also { lives = it })
        add(createLabel(STR_STAT_PERCENTS))
        add(createLabel2().also { percent = it })
        add(createLabel(STR_STAT_NEEDED))
        add(createLabel2().also { needed = it })
        add(createLabel(STR_STAT_BALLS))
        add(createLabel2().also { balls = it })
        isOpaque = true
    }

    private fun createLabel(str: String): JLabel {
        val label = JLabel(String.format("%s: ", game.messages[str]))
        label.horizontalAlignment = SwingConstants.LEFT
        font = Font(Font.MONOSPACED, Font.PLAIN, SIZE_SMALL)
        label.background = Color.BLACK
        label.foreground = Color.LIGHT_GRAY
        label.isOpaque = true
        return label
    }

    private fun createLabel2(): JLabel {
        val label = JLabel("")
        label.horizontalAlignment = SwingConstants.LEFT
        font = Font(Font.MONOSPACED, Font.PLAIN, SIZE_BIG)
        label.background = Color.BLACK
        label.foreground = Color.WHITE
        label.isOpaque = true
        return label
    }

    override fun paint(g: Graphics) {
        val field = game.getField()
        val player = field.player ?: return
        score!!.text = player.score.toString()
        level!!.text = field.levelNum.toString()
        lives!!.text = player.lives.toString()
        percent!!.text = field.percent.toString()
        needed!!.text = field.level!!.percent.toString()
        balls!!.text = field.getBalls().size.toString()
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
