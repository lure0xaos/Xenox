package gargoyle.xenox.util.ini

import gargoyle.xenox.util.log.Log
import java.io.InputStream
import java.io.Reader
import java.nio.charset.Charset

class PropertyMap {
    private val map: Map<String, String>

    constructor(reader: Reader) {
        this.map = runCatching { reader.readLines() }.onFailure { Log.error(it, it.localizedMessage) }.getOrThrow()
            .map { it.trim() }
            .filter { it.isNotBlank() && !it.startsWith("#") && !it.startsWith("//") }
            .associate { it.substringBefore('=').trim() to (it.substringAfter('=').trim()) }.toMap()
    }

    constructor(stream: InputStream, charset: Charset) : this(stream.reader(charset))

    fun entrySet(): Set<Map.Entry<String, String>> =
        map.entries.map { (key, value) ->
            object : Map.Entry<String, String> {
                override val key: String
                    get() = key
                override val value: String
                    get() = value

            }
        }.toSortedSet { (key1), (key2) -> String.CASE_INSENSITIVE_ORDER.compare(key1, key2) }

    operator fun get(key: String): String? =
        map[key]

    operator fun get(key: String, defaultValue: String = ""): String =
        map[key] ?: defaultValue

    operator fun get(key: String, defaultValue: Int = 0): Int =
        map[key]?.toInt() ?: defaultValue

    operator fun get(key: String, defaultValue: Double = 0.0): Double =
        map[key]?.toDouble() ?: defaultValue

    operator fun <T : Any> get(key: String, defaultValue: T, parser: (String) -> T): T =
        map[key]?.let { parser(it) } ?: defaultValue

}
