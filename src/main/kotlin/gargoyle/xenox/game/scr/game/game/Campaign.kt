package gargoyle.xenox.game.scr.game.game

import gargoyle.xenox.util.ini.PropertyMap
import gargoyle.xenox.util.log.Log
import gargoyle.xenox.util.res.Res
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset

class Campaign(url: URL, charset: Charset) {
    private val levels: MutableList<Level> = ArrayList()
    var currentLevelNumber = 0
        private set

    init {
        try {
            InputStreamReader(url.openStream(), charset).use { reader ->
                val file = PropertyMap(reader)
                for (entry in file.entrySet()) {
                    val levelUrl = Res.url(url, entry.value!!.trim())
                    if (levelUrl != null) {
                        levels.add(Level(levelUrl, charset))
                    }
                }
            }
        } catch (e: IOException) {
            Log.error(String.format("cannot load campaign from %s", url), e)
        }
    }

    val currentLevel: Level?
        get() = getLevel(currentLevelNumber)

    private fun getLevel(num: Int): Level? {
        return if (num < levels.size) levels[num] else null
    }

    fun init() {
        currentLevelNumber = 0
    }

    operator fun next(): Boolean {
        currentLevelNumber++
        return currentLevelNumber < levels.size
    }
}
