package gargoyle.xenox.util.config;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

public abstract class Config extends Preferences implements Serializable {
    private Preferences preferences;

    protected Config() {
        try {
            preferences = Preferences.userNodeForPackage(getClass()).node(getClass().getSimpleName());
        } catch (SecurityException e) {
            preferences =
                    MemoryPreferences.userNodeForPackage(getClass()).node(getClass().getSimpleName());
        }
    }

    @Override
    public String absolutePath() {
        return preferences.absolutePath();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        return Objects.equals(preferences, obj);
    }

    @Override
    public void put(String key, String value) {
        preferences.put(key, value);
    }

    @Override
    public String[] childrenNames() throws BackingStoreException {
        return preferences.childrenNames();
    }

    @Override
    public void clear() throws BackingStoreException {
        preferences.clear();
    }

    @Override
    public String get(String key, String def) {
        return preferences.get(key, def);
    }

    @Override
    public void remove(String key) {
        preferences.remove(key);
    }

    @Override
    public void putInt(String key, int value) {
        preferences.putInt(key, value);
    }

    @Override
    public void flush() throws BackingStoreException {
        preferences.flush();
    }

    @Override
    public int getInt(String key, int def) {
        return preferences.getInt(key, def);
    }

    @Override
    public void putLong(String key, long value) {
        preferences.putLong(key, value);
    }

    @Override
    public long getLong(String key, long def) {
        return preferences.getLong(key, def);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        preferences.putBoolean(key, value);
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return preferences.getBoolean(key, def);
    }

    @Override
    public void putFloat(String key, float value) {
        preferences.putFloat(key, value);
    }

    @Override
    public float getFloat(String key, float def) {
        return preferences.getFloat(key, def);
    }

    @Override
    public int hashCode() {
        return preferences.hashCode();
    }

    @Override
    public boolean isUserNode() {
        return preferences.isUserNode();
    }

    @Override
    public String[] keys() throws BackingStoreException {
        return preferences.keys();
    }

    @Override
    public String name() {
        return preferences.name();
    }

    @Override
    public void putDouble(String key, double value) {
        preferences.putDouble(key, value);
    }

    @Override
    public double getDouble(String key, double def) {
        return preferences.getDouble(key, def);
    }

    @Override
    public Preferences parent() {
        return preferences.parent();
    }

    @Override
    public void putByteArray(String key, byte[] value) {
        preferences.putByteArray(key, value);
    }

    @Override
    public byte[] getByteArray(String key, byte[] def) {
        return preferences.getByteArray(key, def);
    }

    @Override
    public Preferences node(String pathName) {
        return preferences.node(pathName);
    }

    @Override
    public boolean nodeExists(String pathName) throws BackingStoreException {
        return preferences.nodeExists(pathName);
    }

    @Override
    public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
        preferences.addPreferenceChangeListener(pcl);
    }

    @Override
    public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
        preferences.removePreferenceChangeListener(pcl);
    }

    @Override
    public void addNodeChangeListener(NodeChangeListener ncl) {
        preferences.addNodeChangeListener(ncl);
    }

    @Override
    public void removeNodeChangeListener(NodeChangeListener ncl) {
        preferences.removeNodeChangeListener(ncl);
    }

    @Override
    public void removeNode() throws BackingStoreException {
        preferences.removeNode();
    }

    @Override
    public void exportNode(OutputStream os) throws IOException, BackingStoreException {
        preferences.exportNode(os);
    }

    @Override
    public void exportSubtree(OutputStream os) throws IOException, BackingStoreException {
        preferences.exportSubtree(os);
    }

    @Override
    public void sync() throws BackingStoreException {
        preferences.sync();
    }

    @Override
    public String toString() {
        return preferences.toString();
    }
}
