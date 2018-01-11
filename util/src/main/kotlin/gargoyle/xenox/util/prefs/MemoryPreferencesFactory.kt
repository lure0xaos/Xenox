package gargoyle.xenox.util.prefs

import java.util.prefs.Preferences
import java.util.prefs.PreferencesFactory

object MemoryPreferencesFactory : PreferencesFactory {
    private val systemRoot by lazy { MemoryPreferences(null, "") }
    private val userRoot by lazy { MemoryPreferences(null, "") }

    override fun systemRoot(): Preferences = systemRoot

    override fun userRoot(): Preferences = userRoot

}
