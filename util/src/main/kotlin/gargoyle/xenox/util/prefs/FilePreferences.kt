package gargoyle.xenox.util.prefs

import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.prefs.AbstractPreferences
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.reader
import kotlin.io.path.writer

internal class FilePreferences(parent: AbstractPreferences?, name: String) : AbstractPreferences(parent, name) {
    private val properties: Properties = Properties()
    private val dir: Path = Path(System.getProperty("user.home", "."))
    private val file: Path = dir.resolve(absolutePath() + "." + EXT)

    private fun read(): Properties =
        properties.apply { file.reader(Charsets.UTF_8).use { load(it) } }

    private fun write(): Unit =
        file.writer(Charsets.UTF_8).use { properties.store(it, Date().toString()) }

    override fun putSpi(key: String, value: String) {
        read()
        properties[key] = value
        write()
    }

    override fun getSpi(key: String): String? =
        read().let { properties[key]?.toString() }

    override fun removeSpi(key: String) {
        properties.remove(key)
        write()
    }

    override fun removeNodeSpi() {
        file.deleteIfExists()
    }

    override fun keysSpi(): Array<String> =
        properties.keys.map { it.toString() }.toTypedArray()

    override fun childrenNamesSpi(): Array<String> =
        Files.find(dir, 0, { path, _ ->
            path.startsWith(file.toString().substringBeforeLast(".$EXT"))
        }).map {
            dir.relativize(it).fileName.toString().substringBeforeLast(".$EXT")
        }.toList().toTypedArray()

    override fun childSpi(name: String): AbstractPreferences =
        FilePreferences(this, name)

    override fun syncSpi() {
        read()
    }

    override fun flushSpi() {
        write()
    }

    companion object {
        private const val EXT = "prefs"
    }
}
