package gargoyle.xenox.campaign.level1

import gargoyle.xenox.info.LevelInfo
import java.net.URL

object TestLevelInfo : LevelInfo {
    override val title: String = "Test"
    override val balls: Int = 5
    override val width: Int = 100
    override val height: Int = 50
    override val lives: Int = 10
    override val percent: Int = 80
    override val speed: Int = 50
    override val cover: URL? = javaClass.getResource("cover1.jpg")
    override val image: URL? = javaClass.getResource("image1.jpg")
    override val music: URL? = javaClass.getResource("music1.au")
}
