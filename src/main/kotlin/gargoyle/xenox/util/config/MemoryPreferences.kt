package gargoyle.xenox.util.config

import gargoyle.xenox.util.log.Log
import java.util.prefs.AbstractPreferences
import java.util.prefs.Preferences
import java.util.prefs.PreferencesFactory

internal class MemoryPreferences(parent: AbstractPreferences?, name: String?) : AbstractPreferences(parent, name) {
    private val children: MutableMap<String, Preferences> = mutableMapOf()
    private val values: MutableMap<String, String> = mutableMapOf()

    init {
        Log.debug(String.format("MemoryPreferences.MemoryPreferences(%s, %s)", parent, name))
    }

    override fun putSpi(key: String, value: String) {
        values[key] = value
    }

    override fun getSpi(key: String): String {
        val value = values[key]
        Log.debug(String.format("get: %s=%s", key, value))
        return value!!
    }

    override fun removeSpi(key: String) {
        values.remove(key)
    }

    override fun removeNodeSpi() {}
    override fun keysSpi(): Array<String> {
        return values.keys.toTypedArray()
    }

    override fun childrenNamesSpi(): Array<String> {
        return children.keys.toTypedArray()
    }

    override fun childSpi(name: String): AbstractPreferences {
        Log.debug(String.format("MemoryPreferences.node(%s)", name))
        val n = MemoryPreferences(this, name)
        children[name] = n
        return n
    }

    override fun syncSpi() {}
    override fun flushSpi() {}

    companion object {
        private val factory: PreferencesFactory = MemoryPreferencesFactory()
        fun userNodeForPackage(clazz: Class<*>): Preferences {
            return userRoot().node(clazz.canonicalName)
        }

        fun userRoot(): Preferences {
            return factory.userRoot()
        }
    }
}
