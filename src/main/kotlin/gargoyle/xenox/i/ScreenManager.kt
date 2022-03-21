package gargoyle.xenox.i

import gargoyle.xenox.game.sys.JScreen

interface ScreenManager {
    fun screenGet(name: String): JScreen?
    fun screenShow(name: String)
}
