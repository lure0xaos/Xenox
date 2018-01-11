package gargoyle.xenox.game.scr.menu.config

import gargoyle.xenox.Xenox
import gargoyle.xenox.game.sys.Controls
import gargoyle.xenox.game.sys.Controls.KeyConfig
import gargoyle.xenox.game.sys.JScreen
import gargoyle.xenox.game.sys.gui.JKeyButton
import gargoyle.xenox.util.i18n.get
import java.awt.BorderLayout
import java.awt.GridLayout
import java.util.*
import javax.swing.*

class JControls(parent: JScreen) : JDialog(
    SwingUtilities.getWindowAncestor(parent), parent.messages[JOptions.STR_OPTIONS],
    ModalityType.APPLICATION_MODAL
) {
    init {
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        layout = BorderLayout()
        val tabConfigs = JTabbedPane()
        parent.controls.configs.indices.forEachIndexed { index, c ->
            addConfigPanel(parent.controls, parent.messages, tabConfigs, index + 1, c, parent.controls.configs[c])
        }
        val pnlNewConfig = JPanel()
        tabConfigs.addTab(parent.messages[STR_CONFIG_PLUS], pnlNewConfig)
        tabConfigs.addChangeListener {
            if (tabConfigs.selectedComponent === pnlNewConfig) {
                val keyConfig = parent.controls.newConfig(0, 0, 0, 0, 0)
                val c = parent.controls.configs.size
                tabConfigs.selectedIndex = c - 1
                parent.controls.configs.add(keyConfig)
                addConfigPanel(parent.controls, parent.messages, tabConfigs, c + 1, c, keyConfig)
                pack()
                tabConfigs.selectedIndex = c
            }
        }
        val pnlButtons = JPanel(GridLayout(1, 2))
        pnlButtons.add(JButton(parent.messages[Xenox.STR_OK]).apply { addActionListener { doHide() } })
        pnlButtons.add(JButton(parent.messages[Xenox.STR_CANCEL]).apply { addActionListener { doHide() } })
        add(tabConfigs, BorderLayout.CENTER)
        add(pnlButtons, BorderLayout.SOUTH)
    }

    private fun doHide() {
        isVisible = false
    }

    companion object {
        const val STR_CONTROLS: String = "controls"
        private const val STR_CONFIG_PLUS = "+"
        private const val STR_CONFIG = "config"
        private const val STR_CONF_ACTION = "action"
        private const val STR_CONF_DOWN = "down"
        private const val STR_CONF_LEFT = "left"
        private const val STR_CONF_RIGHT = "right"
        private const val STR_CONF_UP = "up"
        private fun addConfigPanel(
            controls: Controls, messages: ResourceBundle, tabConfigs: JTabbedPane,
            num: Int, c: Int, keyConfig: KeyConfig
        ) {
            tabConfigs.insertTab(
                "${messages[STR_CONFIG]} $num", null, JPanel(GridLayout(0, 2)).apply {
                    add(JLabel(messages[STR_CONF_ACTION]))
                    add(JKeyButton(keyConfig.getAction()).apply {
                        addLateActionListener { controls.configs[c].setAction(keyCode) }
                    })
                    add(JLabel(messages[STR_CONF_LEFT]))
                    add(JKeyButton(keyConfig.getLeft()).apply {
                        addLateActionListener { controls.configs[c].setLeft(keyCode) }
                    })
                    add(JLabel(messages[STR_CONF_RIGHT]))
                    add(JKeyButton(keyConfig.getRight()).apply {
                        addLateActionListener { controls.configs[c].setRight(keyCode) }
                    })
                    add(JLabel(messages[STR_CONF_UP]))
                    add(JKeyButton(keyConfig.getUp()).apply {
                        addLateActionListener { controls.configs[c].setUp(keyCode) }
                    })
                    add(JLabel(messages[STR_CONF_DOWN]))
                    add(JKeyButton(keyConfig.getDown()).apply {
                        addLateActionListener { controls.configs[c].setDown(keyCode) }
                    })
                }, num.toString(),
                num - 1
            )
        }
    }
}
