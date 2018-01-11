package gargoyle.xenox.game.scr.menu.config

import gargoyle.xenox.Xenox
import gargoyle.xenox.game.sys.JScreen
import gargoyle.xenox.util.i18n.get
import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.*

class JOptions(parent: JScreen) : JDialog(
    SwingUtilities.getWindowAncestor(parent), parent.messages[STR_OPTIONS],
    ModalityType.APPLICATION_MODAL
) {
    init {
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        layout = BorderLayout()
        val chkSound = JCheckBox(parent.messages[STR_SOUND], parent.options.isSound)
        val chkResize = JCheckBox(parent.messages[STR_RESIZE], parent.options.isResize)
        add(JPanel(GridLayout(0, 1)).apply {
            add(chkSound)
            add(chkResize)
        }, BorderLayout.CENTER)
        add(JPanel(GridLayout(1, 2)).apply {
            add(JButton(parent.messages[Xenox.STR_OK]).apply {
                addActionListener {
                    parent.options.isSound = chkSound.isSelected
                    parent.options.isResize = chkResize.isSelected
                    doHide()
                }
            })
            add(JButton(parent.messages[Xenox.STR_CANCEL]).apply {
                addActionListener {
                    chkSound.isSelected = parent.options.isSound
                    chkResize.isSelected = parent.options.isResize
                    doHide()
                }
            })
        }, BorderLayout.SOUTH)
    }

    fun doHide() {
        isVisible = false
    }

    companion object {
        const val STR_OPTIONS: String = "options"
        private const val STR_RESIZE = "resize"
        private const val STR_SOUND = "sound"
    }
}
