package gargoyle.xenox.util.res;

import gargoyle.xenox.util.log.Log;
import gargoyle.xenox.util.res.cache.LoadCache;
import gargoyle.xenox.util.res.load.Loaders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public final class Resources {
    private final LoadCache cache = LoadCache.GLOBAL;
    private final Collection<String> roots = new HashSet<>();
    private final boolean useCache;
//    {
//        roots.add(Resources.classUrl(Resources.class).toExternalForm());
//    }

    public Resources(boolean useCache, Iterable<URL> roots) {
        this.useCache = useCache;
        for (URL url : roots) {
            this.roots.add(url.toExternalForm());
        }
    }

    public Resources(boolean useCache, URL... roots) {
        this.useCache = useCache;
        for (URL url : roots) {
            this.roots.add(url.toExternalForm());
        }
    }

    @Nullable
    private static URL first(@Nullable URL[] urls) {
        if (urls != null) {
            for (URL url : urls) {
                if (url != null) {
                    return url;
                }
            }
        }
        return null;
    }

    public void addRoot(@Nullable URL url) {
        if (url != null) {
            roots.add(url.toExternalForm());
        }
    }
    public void addRoot(@Nullable Class<?> clazz, @Nullable String... names) {
        Objects.requireNonNull(clazz);
        Collection<URL> urls = new HashSet<>();
        if (names == null || names.length == 0) {
            urls.add(Res.classUrl(clazz));
        } else {
            for (String name : names) {
                if (name != null) {
                    urls.add(Res.nearClassUrl(clazz, name));
                }
            }
        }
        for (URL url : urls) {
            if (url != null) {
                roots.add(url.toExternalForm());
            }
        }
    }

    @Nullable
    public <R> R load(@Nullable Class<R> type, @Nullable URL... urls) {
        Objects.requireNonNull(type);
        if (urls == null) {
            return null;
        }
        for (URL url : urls) {
            if (!Res.isUrlOk(url)) {
                continue;
            }
            if (useCache) {
                R cachedResource = cache.get(type, url);
                if (cachedResource != null) {
                    return cachedResource;
                }
            }
            R resource = null;
            try {
                resource = Loaders.load(type, url);
            } catch (IOException e) {
                Log.error(String.format("cannot load %s from %s", type.getSimpleName(), url), e);
            }
            if (resource != null) {
                Log.info(String.format("loaded %s", url));
                return resource;
            }
        }
        Log.error(String.format("%s not found", Arrays.toString(urls)));
        return null;
    }

    @Nullable
    public <R> R load(@Nullable Class<R> type, @Nullable String... names) {
        return load(type, urls(true, names));
    }

    @Nullable
    public URL url(boolean check, @Nullable URL base, @Nullable String value) {
        if (value == null) {
            Log.error("resource null not found");
            return null;
        }
        URL[] urls = urls(check, value);
        for (URL res : urls) {
            if (res != null) {
                return res;
            }
        }
        return Res.url(base, value);
    }

    public URL url(boolean check, @Nullable String... names) {
        return first(urls(check, names));
    }

    @NotNull
    public URL[] urls(boolean check, @Nullable String... names) {
        Collection<URL> urls0 = new HashSet<>();
        if (names != null) {
            for (String location : roots) {
                for (String name : names) {
                    if (name == null) {
                        continue;
                    }
                    URL locationUrl = Res.toURL(location);
                    if (locationUrl == null) {
                        continue;
                    }
                    Collection<URL> okUrls = new ArrayList<>();
                    URL locationNameUrl = Res.nearURL(locationUrl, name);
                    if (locationNameUrl != null) {
                        if (!check || Res.isUrlOk(locationNameUrl)) {
                            okUrls.add(locationNameUrl);
                        }
                    }
                    URL subUrl = Res.subUrl(locationUrl, name);
                    if (subUrl != null) {
                        if (!check || Res.isUrlOk(subUrl)) {
                            okUrls.add(subUrl);
                        }
                    }
                    for (URL url2 : okUrls) {
                        if (!check || Res.isUrlOk(url2)) {
                            urls0.add(url2);
                        }
                    }
                }
            }
        }
        return urls0.toArray(new URL[urls0.size()]);
    }
}
