package gargoyle.xenox.util.res.audio

import java.net.URL

object Audio {
    fun newAudioClip(location: URL): AudioClip {
        return AudioClipImpl(location)
    }
}
