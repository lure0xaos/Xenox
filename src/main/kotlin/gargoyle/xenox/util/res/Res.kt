package gargoyle.xenox.util.res

import gargoyle.xenox.util.log.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.Objects

object Res {
    fun classUrl(clazz: Class<*>): URL {
        Objects.requireNonNull(clazz)
        val url = clazz.classLoader.getResource(String.format("%s.class", clazz.name.replace('.', '/')))
        Objects.requireNonNull(url)
        return url
    }

    fun isUrlOk(url: URL?): Boolean {
        return if (url == null) {
            false
        } else try {
            val urlConnection = url.openConnection()
            if (urlConnection is HttpURLConnection) {
                urlConnection.responseCode / 100 == 2
            } else urlConnection.contentLength > 0
        } catch (e: IOException) {
            false
        }
    }

    fun nearClassUrl(clazz: Class<*>, name: String): URL? {
        return nearURL(classUrl(clazz), name)
    }

    fun nearURL(base: URL, name: String?): URL? {
        return try {
            URL(base, name)
        } catch (e: MalformedURLException) {
            Log.debug(String.format("%s:%s", base, name))
            null
        }
    }

    fun subUrl(url: URL, name: String?): URL? {
        return toURL(String.format("%s/%s", url.toExternalForm(), name))
    }

    fun toURL(url: String): URL? {
        return try {
            URL(url)
        } catch (e: MalformedURLException) {
            Log.debug(url)
            null
        }
    }

    fun url(base: URL?, value: String): URL? {
        var res = toURL(value)
        if (isUrlOk(res)) {
            return res
        }
        res = nearURL(base!!, value)
        if (isUrlOk(res)) {
            return res
        }
        Log.debug(String.format("resource %s not found", value))
        return null
    }
}
