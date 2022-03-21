package gargoyle.xenox.game.scr.menu.config

import gargoyle.xenox.Xenox
import gargoyle.xenox.game.sys.JScreen
import gargoyle.xenox.game.sys.Options
import gargoyle.xenox.util.i18n.Messages
import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JDialog
import javax.swing.JPanel
import javax.swing.SwingUtilities

class JOptions(parent: JScreen) : JDialog(
    SwingUtilities.getWindowAncestor(parent), parent.messages[STR_OPTIONS],
    ModalityType.APPLICATION_MODAL
) {
    private val options: Options?

    init {
        options = parent.options
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        init(parent.messages)
    }

    private fun init(messages: Messages?) {
        layout = BorderLayout()
        val pnlOptions = JPanel(GridLayout(0, 1))
        val chkSound = JCheckBox(messages!![STR_SOUND], options!!.isSound)
        pnlOptions.add(chkSound)
        val chkResize = JCheckBox(messages[STR_RESIZE], options.isResize)
        pnlOptions.add(chkResize)
        val pnlButtons = JPanel(GridLayout(1, 2))
        val btnOk = JButton(messages[Xenox.STR_OK])
        btnOk.addActionListener {
            options.isSound = chkSound.isSelected
            options.isResize = chkResize.isSelected
            isVisible = false
        }
        pnlButtons.add(btnOk)
        val btnCancel = JButton(messages[Xenox.STR_CANCEL])
        btnCancel.addActionListener {
            chkSound.isSelected = options.isSound
            chkResize.isSelected = options.isResize
            isVisible = false
        }
        pnlButtons.add(btnCancel)
        add(pnlOptions, BorderLayout.CENTER)
        add(pnlButtons, BorderLayout.SOUTH)
    }

    companion object {
        const val STR_OPTIONS = "options"
        private const val STR_RESIZE = "resize"
        private const val STR_SOUND = "sound"
    }
}
