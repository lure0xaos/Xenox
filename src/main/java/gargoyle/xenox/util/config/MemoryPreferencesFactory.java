package gargoyle.xenox.util.config;

import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

class MemoryPreferencesFactory implements PreferencesFactory {
    private final MemoryPreferences systemRoot = new MemoryPreferences(null, "");
    private final MemoryPreferences userRoot = new MemoryPreferences(null, "");

    @Override
    public Preferences systemRoot() {
        return systemRoot;
    }

    @Override
    public Preferences userRoot() {
        return userRoot;
    }
}
