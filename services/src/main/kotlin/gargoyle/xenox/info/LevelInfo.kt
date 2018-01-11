package gargoyle.xenox.info

import java.net.URL

interface LevelInfo {
    val title: String
    val width: Int
    val height: Int
    val percent: Int
    val lives: Int
    val balls: Int
    val speed: Int
    val cover: URL?
    val image: URL?
    val music: URL?
}
