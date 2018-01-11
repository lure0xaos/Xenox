package gargoyle.xenox.util.res.load;

import gargoyle.xenox.util.log.Log;

import javax.imageio.ImageIO;

import gargoyle.xenox.util.res.audio.Audio;
import gargoyle.xenox.util.res.audio.AudioClip;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class Loaders {
    private static final Map<Class, Loader<?>> loaders = new HashMap<>();

    static {
        loaders.put(AudioClip.class, Audio::newAudioClip);
        loaders.put(Image.class, ImageIO::read);
        loaders.put(BufferedImage.class, ImageIO::read);
    }

    private Loaders() {
    }

    @SuppressWarnings("unchecked")
    private static <T> Loader<T> getLoader(Class<T> type) {
        return (Loader<T>) loaders.get(type);
    }

    @SuppressWarnings("LawOfDemeter")
    public static <R> R load(Class<R> type, URL url) throws IOException {
        Loader<R> loader = getLoader(type);
        if (loader == null) {
            throw new IOException(String.format("don't know how to load %s", type.getSimpleName()));
        }
        return loader.load(url);
    }

    public static <R> R tryLoad(Class<R> type, URL url) {
        try {
            return load(type, url);
        } catch (IOException e) {
            Log.error(String.format("cannot load %s from %s", type.getSimpleName(), url));
            return null;
        }
    }
}
