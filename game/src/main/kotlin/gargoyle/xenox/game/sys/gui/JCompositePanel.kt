package gargoyle.xenox.game.sys.gui

import java.awt.AlphaComposite
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel

class JCompositePanel(val alpha: Float) : JPanel() {
    public override fun paintComponent(g: Graphics) {
        if (g is Graphics2D) {
            val c = g.composite
            g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 100.0f)
            super.paintComponent(g)
            g.composite = c
        } else {
            super.paintComponent(g)
        }
    }
}
