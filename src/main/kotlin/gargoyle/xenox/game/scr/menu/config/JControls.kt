package gargoyle.xenox.game.scr.menu.config

import gargoyle.xenox.Xenox
import gargoyle.xenox.game.sys.Controls
import gargoyle.xenox.game.sys.Controls.KeyConfig
import gargoyle.xenox.game.sys.JScreen
import gargoyle.xenox.util.gui.JKeyButton
import gargoyle.xenox.util.i18n.Messages
import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.SwingUtilities

class JControls(parent: JScreen) : JDialog(
    SwingUtilities.getWindowAncestor(parent), parent.messages[JOptions.STR_OPTIONS],
    ModalityType.APPLICATION_MODAL
) {
    init {
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        init(parent.messages, parent.controls)
    }

    private fun init(messages: Messages, controls: Controls) {
        layout = BorderLayout()
        val tabConfigs = JTabbedPane()
        var num = 1
        for (c in controls.configs.indices) {
            val keyConfig = controls.configs[c]
            addConfigPanel(controls, messages, tabConfigs, num, c, keyConfig)
            num++
        }
        val pnlNewConfig = JPanel()
        tabConfigs.addTab(messages[STR_CONFIG_PLUS], pnlNewConfig)
        tabConfigs.addChangeListener {
            if (tabConfigs.selectedComponent === pnlNewConfig) {
                val keyConfig = controls.newConfig(0, 0, 0, 0, 0)
                val c = controls.configs.size
                tabConfigs.selectedIndex = c - 1
                controls.configs.add(keyConfig)
                addConfigPanel(controls, messages, tabConfigs, c + 1, c, keyConfig)
                pack()
                tabConfigs.selectedIndex = c
            }
        }
        val pnlButtons = JPanel(GridLayout(1, 2))
        val btnOk = JButton(messages[Xenox.STR_OK])
        btnOk.addActionListener { isVisible = false }
        pnlButtons.add(btnOk)
        val btnCancel = JButton(messages[Xenox.STR_CANCEL])
        btnCancel.addActionListener { isVisible = false }
        pnlButtons.add(btnCancel)
        add(tabConfigs, BorderLayout.CENTER)
        add(pnlButtons, BorderLayout.SOUTH)
    }

    private abstract class BaseKeyListener(
        val controls: Controls,
        val c: Int,
        val btn: JKeyButton
    ) : ActionListener

    private class KeyActionListener(controls: Controls, c: Int, btn: JKeyButton) :
        BaseKeyListener(controls, c, btn) {
        override fun actionPerformed(e: ActionEvent) {
            controls.configs[c].setAction(btn.keyCode)
        }
    }

    private class KeyDownListener(controls: Controls, c: Int, btn: JKeyButton) :
        BaseKeyListener(controls, c, btn) {
        override fun actionPerformed(e: ActionEvent) {
            controls.configs[c].setDown(btn.keyCode)
        }
    }

    private class KeyLeftListener(controls: Controls, c: Int, btn: JKeyButton) :
        BaseKeyListener(controls, c, btn) {
        override fun actionPerformed(e: ActionEvent) {
            controls.configs[c].setLeft(btn.keyCode)
        }
    }

    private class KeyRightListener(controls: Controls, c: Int, btn: JKeyButton) :
        BaseKeyListener(controls, c, btn) {
        override fun actionPerformed(e: ActionEvent) {
            controls.configs[c].setRight(btn.keyCode)
        }
    }

    private class KeyUpListener(controls: Controls, c: Int, btn: JKeyButton) :
        BaseKeyListener(controls, c, btn) {
        override fun actionPerformed(e: ActionEvent) {
            controls.configs[c].setUp(btn.keyCode)
        }
    }

    companion object {
        const val STR_CONTROLS = "controls"
        private const val STR_CONFIG_PLUS = "+"
        private const val STR_CONFIG = "config"
        private const val STR_CONF_ACTION = "action"
        private const val STR_CONF_DOWN = "down"
        private const val STR_CONF_LEFT = "left"
        private const val STR_CONF_RIGHT = "right"
        private const val STR_CONF_UP = "up"
        private fun addConfigPanel(
            controls: Controls, messages: Messages, tabConfigs: JTabbedPane,
            num: Int, c: Int, keyConfig: KeyConfig
        ) {
            val pnlConfig = JPanel(GridLayout(0, 2))
            run {
                pnlConfig.add(JLabel(messages[STR_CONF_ACTION]))
                val btn = JKeyButton(keyConfig.getAction())
                btn.addLateActionListener(KeyActionListener(controls, c, btn))
                pnlConfig.add(btn)
            }
            run {
                pnlConfig.add(JLabel(messages[STR_CONF_LEFT]))
                val btn = JKeyButton(keyConfig.getLeft())
                btn.addLateActionListener(KeyLeftListener(controls, c, btn))
                pnlConfig.add(btn)
            }
            run {
                pnlConfig.add(JLabel(messages[STR_CONF_RIGHT]))
                val btn = JKeyButton(keyConfig.getRight())
                btn.addLateActionListener(KeyRightListener(controls, c, btn))
                pnlConfig.add(btn)
            }
            run {
                pnlConfig.add(JLabel(messages[STR_CONF_UP]))
                val btn = JKeyButton(keyConfig.getUp())
                btn.addLateActionListener(KeyUpListener(controls, c, btn))
                pnlConfig.add(btn)
            }
            pnlConfig.add(JLabel(messages[STR_CONF_DOWN]))
            val btn = JKeyButton(keyConfig.getDown())
            btn.addLateActionListener(KeyDownListener(controls, c, btn))
            pnlConfig.add(btn)
            tabConfigs.insertTab(
                String.format("%s %d", messages[STR_CONFIG], num), null, pnlConfig, num.toString(),
                num - 1
            )
        }
    }
}
