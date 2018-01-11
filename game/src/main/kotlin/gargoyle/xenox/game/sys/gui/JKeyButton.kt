package gargoyle.xenox.game.sys.gui

import java.awt.Toolkit
import java.awt.event.*
import java.util.*
import javax.swing.ComponentInputMap
import javax.swing.InputMap
import javax.swing.JToggleButton
import javax.swing.SwingUtilities

class JKeyButton(keyCode: Int) : JToggleButton() {
    private val lateActionListeners: MutableList<ActionListener> = Collections.synchronizedList(mutableListOf())

    var keyCode: Int
        private set

    init {
        focusTraversalKeysEnabled = false
        setInputMap(WHEN_FOCUSED, InputMap())
        setInputMap(WHEN_IN_FOCUSED_WINDOW, ComponentInputMap(this))
        setInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, InputMap())
        this.keyCode = keyCode
        text = text()
        addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent) {
                onKey(e.keyCode)
            }
        })
        addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    onKey(0)
                }
            }
        })
    }

    fun addLateActionListener(l: ActionListener) {
        lateActionListeners.add(l)
    }

    private fun fireLateActionPerformed(event: ActionEvent) {
        lateActionListeners.forEach { it.actionPerformed(event) }
    }

    private fun onKey(key: Int) {
        if (isSelected || key == 0) {
            isSelected = false
            keyCode = key
            text = text()
            fireLateActionPerformed(ActionEvent(this, ActionEvent.ACTION_PERFORMED, null))
        }
    }

    private fun text(): String =
        KeyEvent.getKeyText(keyCode).let {
            if (it.startsWith(Toolkit.getProperty("AWT.unknown", "Unknown"))) "?" else it
        }
}
