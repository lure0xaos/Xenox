package gargoyle.xenox.util.prefs

import java.util.prefs.Preferences
import java.util.prefs.PreferencesFactory

object FilePreferencesFactory : PreferencesFactory {
    private val systemRoot by lazy { FilePreferences(null, "") }
    private val userRoot by lazy { FilePreferences(null, "") }

    override fun systemRoot(): Preferences = systemRoot

    override fun userRoot(): Preferences = userRoot
}
