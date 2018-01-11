package gargoyle.xenox.util.audio

import java.io.Closeable

interface AudioClip : Closeable {
    fun play()
    fun loop()
    fun stop()
}
