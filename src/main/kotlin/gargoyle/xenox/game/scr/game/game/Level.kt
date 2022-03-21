package gargoyle.xenox.game.scr.game.game

import gargoyle.xenox.Xenox
import gargoyle.xenox.util.ini.PropertyMap
import gargoyle.xenox.util.log.Log
import gargoyle.xenox.util.res.Res
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset

class Level(url: URL, charset: Charset) {
    var title: String? = null
    var balls = DEFAULT_BALLS
    var cover: URL? = null
    var height: Int = Xenox.PF_HEIGHT
    var image: URL? = null
    var lives: Int = Xenox.LIVES
    var music: URL? = null
    var percent = DEFAULT_PERCENT
    var speed = DEFAULT_SPEED
    var width: Int = Xenox.PF_WIDTH

    init {
        try {
            InputStreamReader(url.openStream(), charset).use { reader ->
                val file = PropertyMap(reader)
                title = file[PROP_TITLE]
                balls = file[PARAM_BALLS, DEFAULT_BALLS]
                height = file[PARAM_HEIGHT, Xenox.PF_HEIGHT]
                lives = file[PARAM_LIVES, Xenox.LIVES]
                percent = file[PARAM_PERCENT, DEFAULT_PERCENT]
                speed = file[PARAM_SPEED, DEFAULT_SPEED]
                width = file[PARAM_WIDTH, Xenox.PF_WIDTH]
                cover = Res.url(url, file[PARAM_COVER]!!)
                image = Res.url(url, file[PARAM_IMAGE]!!)
                music = Res.url(url, file[PARAM_MUSIC]!!)
            }
        } catch (e: IOException) {
            Log.error(String.format("cannot load level from %s", url), e)
        }
    }

    companion object {
        private const val DEFAULT_BALLS = 80
        private const val DEFAULT_PERCENT = 80
        private const val DEFAULT_SPEED = 50
        private const val PARAM_BALLS = "balls"
        private const val PARAM_COVER = "cover"
        private const val PARAM_HEIGHT = "height"
        private const val PARAM_IMAGE = "image"
        private const val PARAM_LIVES = "lives"
        private const val PARAM_MUSIC = "music"
        private const val PARAM_PERCENT = "percent"
        private const val PARAM_SPEED = "speed"
        private const val PARAM_WIDTH = "width"
        private const val PROP_TITLE = "title"
    }
}
