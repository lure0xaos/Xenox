package gargoyle.xenox.util.prefs

import java.util.prefs.AbstractPreferences
import java.util.prefs.Preferences

internal class MemoryPreferences(parent: AbstractPreferences?, name: String?) : AbstractPreferences(parent, name) {
    private val children: MutableMap<String, Preferences> = mutableMapOf()
    private val values: MutableMap<String, String> = mutableMapOf()

    override fun putSpi(key: String, value: String) {
        values[key] = value
    }

    override fun getSpi(key: String): String =
        values[key] ?: ""

    override fun removeSpi(key: String) {
        values.remove(key)
    }

    override fun removeNodeSpi() {}
    override fun keysSpi(): Array<String> =
        values.keys.toTypedArray()

    override fun childrenNamesSpi(): Array<String> =
        children.keys.toTypedArray()

    override fun childSpi(name: String): AbstractPreferences =
        MemoryPreferences(this, name).also { children[name] = it }

    override fun syncSpi() {}
    override fun flushSpi() {}

}
