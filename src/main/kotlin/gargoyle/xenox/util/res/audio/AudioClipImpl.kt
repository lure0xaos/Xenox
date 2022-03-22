package gargoyle.xenox.util.res.audio

import java.io.BufferedInputStream
import java.net.URL
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

internal class AudioClipImpl(location: URL) : AudioClip {
    private val clip: Clip

    init {
        clip = AudioSystem.getClip().also {
            location.openConnection().getInputStream().use { stream ->
                BufferedInputStream(stream).use { bufferedInputStream ->
                    AudioSystem.getAudioInputStream(bufferedInputStream).use { sound ->
                        it.open(sound)
                    }
                }
            }
        }
    }

    override fun play() {
        if (clip.isOpen) {
            clip.framePosition = 0
            clip.start()
        }
    }

    override fun loop() {
        if (clip.isOpen) {
            clip.loop(Clip.LOOP_CONTINUOUSLY)
        }
    }

    override fun stop() {
        if (clip.isOpen) {
            clip.stop()
        }
    }

    override fun close() {
        if (clip.isOpen) {
            clip.close()
        }
    }
}
