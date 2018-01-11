package gargoyle.xenox.util.i18n;

import gargoyle.xenox.util.log.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public final class Messages implements Serializable {
    private ResourceBundle bundle;

    public Messages(String name, Locale locale, Charset charset) {
        try {
            bundle = ResourceBundle.getBundle(name, locale, new UTF8Control(charset));
        } catch (MissingResourceException e) {
            Log.error(String.format("resource %s not found", name));
        }
    }

    public String get(String key) {
        if (bundle == null) {
            Log.error(String.format("resource %s.%s not found", "?", key));
            return key;
        }
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            Log.error(String.format("resource %s.%s not found", e.getClassName(), e.getKey()));
            return key;
        }
    }

    private static final class UTF8Control extends ResourceBundle.Control {
        private static final String PROPERTIES = "properties";
        private final Charset charset;

        private UTF8Control(Charset charset) {
            this.charset = charset;
        }

        private static InputStream getInputStream(ClassLoader loader, boolean reload, String resourceName) throws IOException {
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url == null) {
                    throw new IOException(String.format("resource %s not found", resourceName));
                }
                URLConnection connection = url.openConnection();
                if (connection != null) {
                    connection.setUseCaches(false);
                    return connection.getInputStream();
                }
            } else {
                InputStream stream = loader.getResourceAsStream(resourceName);
                if (stream == null) {
                    throw new IOException(String.format("resource %s not found", resourceName));
                }
                return stream;
            }
            throw new IOException(String.format("resource %s not found", resourceName));
        }

        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, PROPERTIES);
            try (InputStream stream = getInputStream(loader, reload, resourceName)) {
                return new PropertyResourceBundle(new InputStreamReader(stream, charset));
            }
        }
    }
}
