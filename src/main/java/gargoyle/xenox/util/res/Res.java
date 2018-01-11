package gargoyle.xenox.util.res;

import gargoyle.xenox.util.log.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public final class Res {
    private Res() {
    }

    @NotNull
    public static URL classUrl(@Nullable Class<?> clazz) {
        Objects.requireNonNull(clazz);
        URL url = clazz.getClassLoader().getResource(String.format("%s.class", clazz.getName().replace('.', '/')));
        Objects.requireNonNull(url);
        return url;
    }

    public static boolean isUrlOk(@Nullable URL url) {
        if (url == null) {
            return false;
        }
        try {
            URLConnection urlConnection = url.openConnection();
            if (urlConnection instanceof HttpURLConnection) {
                return (((HttpURLConnection) urlConnection).getResponseCode() / 100) == 2;
            }
            return urlConnection.getContentLength() > 0;
        } catch (IOException e) {
            return false;
        }
    }

    @Nullable
    public static URL nearClassUrl(@NotNull Class<?> clazz, @NotNull String name) {
        return nearURL(classUrl(clazz), name);
    }

    @Nullable
    public static URL nearURL(@NotNull URL base, @NotNull String name) {
        try {
            return new URL(base, name);
        } catch (MalformedURLException e) {
            Log.debug(String.format("%s:%s", base, name));
            return null;
        }
    }

    @Nullable
    public static URL subUrl(@NotNull URL url, @NotNull String name) {
        return toURL(String.format("%s/%s", url.toExternalForm(), name));
    }

    public static URL toURL(@NotNull String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            Log.debug(url);
            return null;
        }
    }

    @Nullable
    public static URL url(@Nullable URL base, @Nullable String value) {
        if (value == null) {
            Log.error("resource null not found");
            return null;
        }
        URL res = toURL(value);
        if (isUrlOk(res)) {
            return res;
        }
        Objects.requireNonNull(base);
        res = nearURL(base, value);
        if (isUrlOk(res)) {
            return res;
        }
        Log.debug(String.format("resource %s not found", value));
        return null;
    }
}
