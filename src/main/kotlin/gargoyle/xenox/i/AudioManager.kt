package gargoyle.xenox.i

import gargoyle.xenox.util.res.audio.AudioClip
import java.net.URL

interface AudioManager {
    fun musicLoop()
    fun musicPlay()
    fun musicSet(clip: AudioClip)
    fun musicSet(vararg clip: URL)
    fun musicStop()
    fun soundPlay(clip: AudioClip)
    fun soundPlay(vararg clip: URL)
}
