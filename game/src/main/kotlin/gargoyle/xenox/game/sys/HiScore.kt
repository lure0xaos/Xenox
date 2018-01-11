package gargoyle.xenox.game.sys

import gargoyle.xenox.util.config.Config
import gargoyle.xenox.util.log.Log
import java.util.prefs.Preferences

class HiScore {
    private val preferences: Preferences = Config.getPreferences(HiScore::class)
    operator fun get(index: Int): Record {
        val key = index.toString()
        return Record(preferences.node(key)[PARAM_NAME, EMPTY], preferences.node(key).getInt(PARAM_SCORE, 0))
    }

    val records: List<Record>
        get() {
            val records: MutableList<Record> = mutableListOf()
            (0 until size()).forEach { records += get(it) }
            return records
        }
    var userName: String
        get() = preferences.get(PARAM_NAME, EMPTY)
        set(username) = preferences.put(PARAM_NAME, username)

    private fun put(index: Int, name: String, score: Int) {
        val node = preferences.node(index.toString())
        node.put(PARAM_NAME, name)
        node.putInt(PARAM_SCORE, score)
    }

    fun score(name: String, score: Int) {
        put(size(), name, score)
        val table = (0..size()).map { this[it] }.sorted()
        (0 until (table.size)).forEach { put(it, table[it].name, table[it].score) }
    }

    fun size(): Int {
        var size = 0
        try {
            preferences.childrenNames().forEach { key ->
                if (key.toIntOrNull() != null && preferences.node(key)[PARAM_NAME, EMPTY].isNotEmpty()) {
                    size = LINES.coerceAtMost(key.toInt() + 1)
                }
            }
        } catch (e: Exception) {
            Log.error(e, e.localizedMessage)
        }
        return size
    }

    data class Record(val name: String, val score: Int) : Comparable<Record> {

        override fun compareTo(other: Record): Int {
            return other.score.compareTo(score)
        }

        override fun hashCode(): Int {
            val prime = 31
            var result = 1
            result = prime * result + name.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other == null || javaClass != other.javaClass) {
                return false
            }
            return name == (other as Record).name
        }
    }

    companion object {
        const val LINES: Int = 10
        private const val EMPTY = ""
        private const val PARAM_NAME = "name"
        private const val PARAM_SCORE = "score"
    }
}
