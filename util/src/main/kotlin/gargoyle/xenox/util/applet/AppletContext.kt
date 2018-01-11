package gargoyle.xenox.util.applet

import gargoyle.xenox.util.audio.AudioClip
import java.awt.Image
import java.io.InputStream
import java.net.URL
import java.util.*

interface AppletContext {
    val applets: Enumeration<Applet>
    val streamKeys: Iterator<String>
    fun getAudioClip(url: URL): AudioClip
    fun getAudioClip(url: InputStream): AudioClip
    fun getImage(url: URL): Image
    fun getImage(url: InputStream): Image
    fun getApplet(name: String): Applet?
    fun getStream(key: String): InputStream?
    fun setStream(key: String, stream: InputStream)
    fun showDocument(url: URL, target: String = "_self")
    fun showStatus(status: String)
}
