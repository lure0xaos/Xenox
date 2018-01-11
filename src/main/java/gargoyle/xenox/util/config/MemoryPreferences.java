package gargoyle.xenox.util.config;

import gargoyle.xenox.util.log.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

class MemoryPreferences extends AbstractPreferences {
    private static final PreferencesFactory factory = new MemoryPreferencesFactory();
    private final Map<String, Preferences> children = new HashMap<>();
    private final Map<String, String> values = new HashMap<>();

    MemoryPreferences(AbstractPreferences parent, String name) {
        super(parent, name);
        Log.debug(String.format("MemoryPreferences.MemoryPreferences(%s, %s)", parent, name));
    }

    public static Preferences userNodeForPackage(Class<?> clazz) {
        return userRoot().node(clazz.getCanonicalName());
    }

    @SuppressWarnings("WeakerAccess")
    public static Preferences userRoot() {
        return factory.userRoot();
    }

    @Override
    protected void putSpi(String key, String value) {
        values.put(key, value);
    }

    @Override
    protected String getSpi(String key) {
        String value = values.get(key);
        Log.debug(String.format("get: %s=%s", key, value));
        return value;
    }

    @Override
    protected void removeSpi(String key) {
        values.remove(key);
    }

    @Override
    protected void removeNodeSpi() {
    }

    @Override
    protected String[] keysSpi() {
        return values.keySet().toArray(new String[values.size()]);
    }

    @Override
    protected String[] childrenNamesSpi() {
        return children.keySet().toArray(new String[children.size()]);
    }

    @Override
    protected AbstractPreferences childSpi(String name) {
        Log.debug(String.format("MemoryPreferences.node(%s)", name));
        AbstractPreferences n = new MemoryPreferences(this, name);
        children.put(name, n);
        return n;
    }

    @Override
    protected void syncSpi() {
    }

    @Override
    protected void flushSpi() {
    }
}
