package gargoyle.xenox.util.config

import gargoyle.xenox.util.prefs.MemoryPreferencesFactory
import java.util.prefs.Preferences
import java.util.prefs.Preferences.userNodeForPackage
import kotlin.reflect.KClass

object Config {
    fun getPreferences(clazz: KClass<*>): Preferences = try {
        userNodeForPackage(clazz.java).node(clazz.simpleName)
    } catch (e: SecurityException) {
        MemoryPreferencesFactory.userRoot().node(clazz.qualifiedName).node(clazz.simpleName)
    }
}
