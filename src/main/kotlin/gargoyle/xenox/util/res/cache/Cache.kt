package gargoyle.xenox.util.res.cache

import java.util.Collections
import java.util.WeakHashMap

class Cache {
    private val cache = Collections.synchronizedMap(WeakHashMap<String, Any>())
    operator fun <R> get(location: String): R? {
        @Suppress("UNCHECKED_CAST")
        return cache[location] as R?
    }

    fun <R> put(location: String, resource: R) {
        cache[location] = resource
    }

    companion object {
        val GLOBAL = Cache()
    }
}
