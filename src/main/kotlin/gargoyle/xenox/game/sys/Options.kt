package gargoyle.xenox.game.sys

import gargoyle.xenox.util.config.Config

class Options : Config() {
    var isResize: Boolean
        get() = getBoolean(PARAM_RESIZE, false)
        set(resize) {
            putBoolean(PARAM_RESIZE, resize)
        }
    var isSound: Boolean
        get() = getBoolean(PARAM_SOUND, true)
        set(sound) {
            putBoolean(PARAM_SOUND, sound)
        }

    companion object {
        private const val PARAM_RESIZE = "resize"
        private const val PARAM_SOUND = "sound"
        private const val serialVersionUID = -2434365584897663718L
    }
}
