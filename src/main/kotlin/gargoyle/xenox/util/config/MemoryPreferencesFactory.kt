package gargoyle.xenox.util.config

import java.util.prefs.Preferences
import java.util.prefs.PreferencesFactory

internal class MemoryPreferencesFactory : PreferencesFactory {
    private val systemRoot = MemoryPreferences(null, "")
    private val userRoot = MemoryPreferences(null, "")
    override fun systemRoot(): Preferences {
        return systemRoot
    }

    override fun userRoot(): Preferences {
        return userRoot
    }
}
