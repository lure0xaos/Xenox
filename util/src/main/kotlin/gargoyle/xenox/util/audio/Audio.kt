package gargoyle.xenox.util.audio

import java.io.InputStream
import java.net.URL

object Audio {
    fun newAudioClip(location: URL): AudioClip = AudioClipImpl(location)
    fun newAudioClip(location: InputStream): AudioClip = AudioClipImpl(location)
}
