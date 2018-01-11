package gargoyle.xenox.game.sys

import gargoyle.xenox.util.config.Config
import java.util.prefs.Preferences

class Options {
    private val preferences: Preferences = Config.getPreferences(Options::class)
    var isResize: Boolean
        get() = preferences.getBoolean(PARAM_RESIZE, false)
        set(resize) {
            preferences.putBoolean(PARAM_RESIZE, resize)
            preferences.flush()
        }
    var isSound: Boolean
        get() = preferences.getBoolean(PARAM_SOUND, true)
        set(sound) {
            preferences.putBoolean(PARAM_SOUND, sound)
            preferences.flush()
        }

    companion object {
        private const val PARAM_RESIZE = "resize"
        private const val PARAM_SOUND = "sound"
    }
}
