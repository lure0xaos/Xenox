package gargoyle.xenox.util.config

import java.io.IOException
import java.io.OutputStream
import java.io.Serializable
import java.util.prefs.BackingStoreException
import java.util.prefs.NodeChangeListener
import java.util.prefs.PreferenceChangeListener
import java.util.prefs.Preferences

abstract class Config protected constructor() : Preferences(), Serializable {
    private var preferences: Preferences? = null

    init {
        preferences = try {
            userNodeForPackage(javaClass).node(javaClass.simpleName)
        } catch (e: SecurityException) {
            MemoryPreferences.userNodeForPackage(javaClass).node(javaClass.simpleName)
        }
    }

    override fun absolutePath(): String {
        return preferences!!.absolutePath()
    }

    override fun equals(other: Any?): Boolean {
        return preferences == other
    }

    override fun put(key: String, value: String) {
        preferences!!.put(key, value)
    }

    override fun childrenNames(): Array<String> {
        return preferences!!.childrenNames()
    }

    override fun clear() {
        preferences!!.clear()
    }

    override fun get(key: String, def: String): String {
        return preferences!![key, def]
    }

    override fun remove(key: String) {
        preferences!!.remove(key)
    }

    override fun putInt(key: String, value: Int) {
        preferences!!.putInt(key, value)
    }

    override fun flush() {
        preferences!!.flush()
    }

    override fun getInt(key: String, def: Int): Int {
        return preferences!!.getInt(key, def)
    }

    override fun putLong(key: String, value: Long) {
        preferences!!.putLong(key, value)
    }

    override fun getLong(key: String, def: Long): Long {
        return preferences!!.getLong(key, def)
    }

    override fun putBoolean(key: String, value: Boolean) {
        preferences!!.putBoolean(key, value)
    }

    override fun getBoolean(key: String, def: Boolean): Boolean {
        return preferences!!.getBoolean(key, def)
    }

    override fun putFloat(key: String, value: Float) {
        preferences!!.putFloat(key, value)
    }

    override fun getFloat(key: String, def: Float): Float {
        return preferences!!.getFloat(key, def)
    }

    override fun hashCode(): Int {
        return preferences.hashCode()
    }

    override fun isUserNode(): Boolean {
        return preferences!!.isUserNode
    }

    override fun keys(): Array<String> {
        return preferences!!.keys()
    }

    override fun name(): String {
        return preferences!!.name()
    }

    override fun putDouble(key: String, value: Double) {
        preferences!!.putDouble(key, value)
    }

    override fun getDouble(key: String, def: Double): Double {
        return preferences!!.getDouble(key, def)
    }

    override fun parent(): Preferences {
        return preferences!!.parent()
    }

    override fun putByteArray(key: String, value: ByteArray) {
        preferences!!.putByteArray(key, value)
    }

    override fun getByteArray(key: String, def: ByteArray): ByteArray {
        return preferences!!.getByteArray(key, def)
    }

    override fun node(pathName: String): Preferences {
        return preferences!!.node(pathName)
    }

    override fun nodeExists(pathName: String): Boolean {
        return preferences!!.nodeExists(pathName)
    }

    override fun addPreferenceChangeListener(pcl: PreferenceChangeListener) {
        preferences!!.addPreferenceChangeListener(pcl)
    }

    override fun removePreferenceChangeListener(pcl: PreferenceChangeListener) {
        preferences!!.removePreferenceChangeListener(pcl)
    }

    override fun addNodeChangeListener(ncl: NodeChangeListener) {
        preferences!!.addNodeChangeListener(ncl)
    }

    override fun removeNodeChangeListener(ncl: NodeChangeListener) {
        preferences!!.removeNodeChangeListener(ncl)
    }

    override fun removeNode() {
        preferences!!.removeNode()
    }

    override fun exportNode(os: OutputStream) {
        preferences!!.exportNode(os)
    }

    override fun exportSubtree(os: OutputStream) {
        preferences!!.exportSubtree(os)
    }

    override fun sync() {
        preferences!!.sync()
    }

    override fun toString(): String {
        return preferences.toString()
    }
}
