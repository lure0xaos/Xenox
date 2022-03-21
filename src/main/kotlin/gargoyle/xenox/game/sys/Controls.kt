package gargoyle.xenox.game.sys

import gargoyle.xenox.util.config.Config
import gargoyle.xenox.util.log.Log
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.Serializable
import java.util.Collections
import java.util.prefs.BackingStoreException
import java.util.prefs.Preferences

class Controls : Config(), KeyListener {
    private val keyConfigs: MutableList<KeyConfig> = mutableListOf()

    var isAction = false
        private set

    var isDown = false
        private set

    var isLeft = false
        private set

    var isRight = false
        private set

    var isUp = false
        private set

    init {
        reset()
    }

    fun addDefaultConfig() {
        newConfig(KeyEvent.VK_ENTER, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT)
    }

    val configs: MutableList<KeyConfig>
        get() = Collections.unmodifiableList(keyConfigs)

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

    fun newConfig(action: Int, up: Int, down: Int, left: Int, right: Int): KeyConfig {
        val keyConfig = KeyConfig(this, action, up, down, left, right)
        keyConfigs.add(keyConfig)
        return keyConfig
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
    ) : Serializable {
        private var num = 0
        private var preferences: Preferences?

        init {
            preferences = controls.node(num.toString())
            try {
                while (controls.nodeExists(num.toString())) {
                    if (action == getAction() && up == getUp() && down == getDown() && left == getLeft() && right == getRight()) {
                        break
                    }
                    num++
                    preferences = controls.node(num.toString())
                }
            } catch (e: BackingStoreException) {
                Log.error(e.localizedMessage, e)
            }
            setAction(action)
            setUp(up)
            setDown(down)
            setLeft(left)
            setRight(right)
        }

        fun getAction(): Int {
            return preferences!!.getInt(PARAM_ACTION, KeyEvent.VK_ENTER)
        }

        fun setAction(action: Int) {
            preferences!!.putInt(PARAM_ACTION, action)
        }

        fun getDown(): Int {
            return preferences!!.getInt(PARAM_DOWN, KeyEvent.VK_DOWN)
        }

        fun setDown(down: Int) {
            preferences!!.putInt(PARAM_DOWN, down)
        }

        fun getLeft(): Int {
            return preferences!!.getInt(PARAM_LEFT, KeyEvent.VK_LEFT)
        }

        fun setLeft(left: Int) {
            preferences!!.putInt(PARAM_LEFT, left)
        }

        fun getRight(): Int {
            return preferences!!.getInt(PARAM_RIGHT, KeyEvent.VK_RIGHT)
        }

        fun setRight(right: Int) {
            preferences!!.putInt(PARAM_RIGHT, right)
        }

        fun getUp(): Int {
            return preferences!!.getInt(PARAM_UP, KeyEvent.VK_UP)
        }

        fun setUp(up: Int) {
            preferences!!.putInt(PARAM_UP, up)
        }

        companion object {
            private const val serialVersionUID = 8127782339105594052L
        }
    }

    companion object {
        private const val PARAM_ACTION = "action"
        private const val PARAM_DOWN = "down"
        private const val PARAM_LEFT = "left"
        private const val PARAM_RIGHT = "right"
        private const val PARAM_UP = "up"
        private const val serialVersionUID = -6095549099825656180L
    }
}
