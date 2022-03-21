package gargoyle.xenox.game.sys

import gargoyle.xenox.util.config.Config
import gargoyle.xenox.util.log.Log
import java.io.Serializable
import java.util.Collections
import java.util.prefs.BackingStoreException
import java.util.regex.Pattern
import java.util.stream.Collectors
import java.util.stream.IntStream

class HiScore : Config() {
    operator fun get(index: Int): Record {
        val key = index.toString()
        return Record(node(key)[PARAM_NAME, EMPTY], node(key).getInt(PARAM_SCORE, 0))
    }

    val records: List<Record>
        get() {
            val size = size()
            val records: MutableList<Record> = ArrayList(size)
            for (i in 0 until size) {
                records.add(get(i))
            }
            return Collections.unmodifiableList(records)
        }
    var userName: String?
        get() = get(PARAM_NAME, EMPTY)
        set(username) {
            put(PARAM_NAME, username!!)
        }

    private fun put(index: Int, name: String?, score: Int) {
        val node = node(index.toString())
        node.put(PARAM_NAME, name)
        node.putInt(PARAM_SCORE, score)
    }

    fun score(name: String?, score: Int) {
        put(size(), name, score)
        val table = IntStream.range(0, size()).mapToObj { index: Int -> this[index] }
            .sorted().collect(Collectors.toList())
        IntStream.range(0, LINES.coerceAtMost(table.size))
            .forEach { index: Int -> put(index, table[index].name, table[index].score) }
    }

    fun size(): Int {
        var size = 0
        try {
            for (key in childrenNames()) {
                if (DIGITS.matcher(key).matches()) {
                    size = LINES.coerceAtMost(key.toInt() + 1)
                    val name = node(key)[PARAM_NAME, EMPTY]
                    if (name != null && name.isEmpty()) {
                        break
                    }
                }
            }
        } catch (e: BackingStoreException) {
            Log.error(e.localizedMessage, e)
        }
        return size
    }

    class Record(name: String, score: Int) : Serializable, Comparable<Record> {
        val name: String?
        val score: Int

        init {
            this.name = name
            this.score = score
        }

        override fun compareTo(other: Record): Int {
            return other.score.compareTo(score)
        }

        override fun hashCode(): Int {
            val prime = 31
            var result = 1
            result = prime * result + (name?.hashCode() ?: 0)
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other == null || javaClass != other.javaClass) {
                return false
            }
            return if (name == null) (other as Record).name == null else name == (other as Record).name
        }
    }

    companion object {
        const val LINES = 10
        private val DIGITS = Pattern.compile("[0-9]+")
        private const val EMPTY = ""
        private const val PARAM_NAME = "name"
        private const val PARAM_SCORE = "score"
        private const val serialVersionUID = -8306160552821206611L
    }
}
