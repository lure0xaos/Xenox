package gargoyle.xenox.util.applet

import gargoyle.xenox.util.res.audio.AudioClip
import java.awt.Image
import java.io.InputStream
import java.net.URL
import java.util.Enumeration

interface AppletStub {
    fun getAudioClip(url: URL): AudioClip
    fun getImage(url: URL): Image
    fun getApplet(name: String): Applet?
    val applets: Enumeration<Applet>
    val documentBase: URL?
    val codeBase: URL?
    fun showStatus(status: String)
    fun setStream(key: String, stream: InputStream)
    fun getStream(key: String): InputStream?
    val streamKeys: Iterator<String>
    fun showDocument(url: URL)
    fun showDocument(url: URL, target: String)
    fun getParameter(name: String): String?
    val appletContext: AppletContext
    fun appletResize(width: Int, height: Int)
}
