package gargoyle.xenox.i

import gargoyle.xenox.util.audio.AudioClip

interface AudioManager {
    fun musicLoop()
    fun musicPlay()
    fun musicSet(clip: AudioClip)
    fun musicStop()
    fun soundPlay(clip: AudioClip)
}
