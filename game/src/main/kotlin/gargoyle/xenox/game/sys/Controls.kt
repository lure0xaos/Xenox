package gargoyle.xenox.game.sys

import gargoyle.xenox.util.config.Config
import gargoyle.xenox.util.log.Log
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.prefs.Preferences

class Controls : KeyListener {
    private val preferences: Preferences = Config.getPreferences(Controls::class)
    private val keyConfigs: MutableList<KeyConfig> = mutableListOf()

    var isAction: Boolean = false
        private set

    var isDown: Boolean = false
        private set

    var isLeft: Boolean = false
        private set

    var isRight: Boolean = false
        private set

    var isUp: Boolean = false
        private set

    init {
        reset()
    }

    fun addDefaultConfig() {
        newConfig(KeyEvent.VK_ENTER, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT)
    }

    val configs: MutableList<KeyConfig>
        get() = keyConfigs

    override fun keyTyped(e: KeyEvent) {}
    override fun keyPressed(e: KeyEvent) {
        val key = e.keyCode
        reset()
        for (keyConfig in keyConfigs) {
            if (key == keyConfig.getAction()) {
                isAction = true
            }
            if (key == keyConfig.getDown()) {
                isDown = true
            }
            if (key == keyConfig.getLeft()) {
                isLeft = true
            }
            if (key == keyConfig.getRight()) {
                isRight = true
            }
            if (key == keyConfig.getUp()) {
                isUp = true
            }
        }
    }

    override fun keyReleased(e: KeyEvent) {
        val key = e.keyCode
        for (keyConfig in keyConfigs) {
            if (key == keyConfig.getAction()) {
                isAction = false
            }
            if (key == keyConfig.getDown()) {
                isDown = false
            }
            if (key == keyConfig.getLeft()) {
                isLeft = false
            }
            if (key == keyConfig.getRight()) {
                isRight = false
            }
            if (key == keyConfig.getUp()) {
                isUp = false
            }
        }
    }

    fun newConfig(action: Int, up: Int, down: Int, left: Int, right: Int): KeyConfig =
        KeyConfig(this, action, up, down, left, right).also {
            keyConfigs += it
        }

    fun reset() {
        isAction = false
        isDown = false
        isUp = false
        isRight = false
        isLeft = false
    }

    class KeyConfig internal constructor(
        controls: Controls, action: Int, up: Int, down: Int, left: Int,
        right: Int
    ) {
        private var num = 0
        private var preferences: Preferences

        init {
            preferences = controls.preferences.node(num.toString())
            try {
                while (controls.preferences.nodeExists(num.toString())) {
                    if (action == getAction() && up == getUp() && down == getDown() && left == getLeft() && right == getRight()) {
                        break
                    }
                    num++
                    preferences = controls.preferences.node(num.toString())
                }
            } catch (e: Exception) {
                Log.error(e, e.localizedMessage)
            }
            setAction(action)
            setUp(up)
            setDown(down)
            setLeft(left)
            setRight(right)
        }

        fun getAction(): Int {
            return preferences.getInt(PARAM_ACTION, KeyEvent.VK_ENTER)
        }

        fun setAction(action: Int) {
            preferences.putInt(PARAM_ACTION, action)
            preferences.flush()
        }

        fun getDown(): Int {
            return preferences.getInt(PARAM_DOWN, KeyEvent.VK_DOWN)
        }

        fun setDown(down: Int) {
            preferences.putInt(PARAM_DOWN, down)
            preferences.flush()
        }

        fun getLeft(): Int {
            return preferences.getInt(PARAM_LEFT, KeyEvent.VK_LEFT)
        }

        fun setLeft(left: Int) {
            preferences.putInt(PARAM_LEFT, left)
            preferences.flush()
        }

        fun getRight(): Int {
            return preferences.getInt(PARAM_RIGHT, KeyEvent.VK_RIGHT)
        }

        fun setRight(right: Int) {
            preferences.putInt(PARAM_RIGHT, right)
            preferences.flush()
        }

        fun getUp(): Int {
            return preferences.getInt(PARAM_UP, KeyEvent.VK_UP)
        }

        fun setUp(up: Int) {
            preferences.putInt(PARAM_UP, up)
            preferences.flush()
        }

    }

    companion object {
        private const val PARAM_ACTION = "action"
        private const val PARAM_DOWN = "down"
        private const val PARAM_LEFT = "left"
        private const val PARAM_RIGHT = "right"
        private const val PARAM_UP = "up"
    }
}
