package gargoyle.xenox.i

import gargoyle.xenox.game.sys.Controls
import gargoyle.xenox.game.sys.Options
import gargoyle.xenox.info.CampaignInfo
import java.util.*

interface ScreenAssets {
    val messages: ResourceBundle
    val controls: Controls
    val audio: AudioManager
    val screens: ScreenManager
    val options: Options
    val title: String
    var campaign: CampaignInfo
}
