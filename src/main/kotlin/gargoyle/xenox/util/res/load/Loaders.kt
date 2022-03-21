package gargoyle.xenox.util.res.load

import gargoyle.xenox.util.log.Log
import gargoyle.xenox.util.res.audio.Audio
import gargoyle.xenox.util.res.audio.AudioClip
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.IOException
import java.net.URL
import javax.imageio.ImageIO

object Loaders {
    private val loaders: MutableMap<Class<*>, Loader<*>> = HashMap()

    init {
        loaders[AudioClip::class.java] = Loader<Any> { Audio.newAudioClip(it) }
        loaders[Image::class.java] = Loader<Any> { ImageIO.read(it) }
        loaders[BufferedImage::class.java] = Loader<Any> { ImageIO.read(it) }
    }

    private fun <T> getLoader(type: Class<T>): Loader<T>? {
        @Suppress("UNCHECKED_CAST")
        return loaders[type]!! as Loader<T>?
    }

    fun <R> load(type: Class<R>, url: URL): R? {
        val loader = getLoader(type) ?: throw IOException(String.format("don't know how to load %s", type.simpleName))
        return loader.load(url)
    }

    fun <R> tryLoad(type: Class<R>, url: URL): R? {
        return try {
            load(type, url)
        } catch (e: IOException) {
            Log.error(String.format("cannot load %s from %s", type.simpleName, url))
            null
        }
    }
}
