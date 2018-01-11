package gargoyle.xenox.util.applet

import gargoyle.xenox.util.audio.AudioClip
import java.awt.Image
import java.io.InputStream
import java.net.URL

interface Applet {
    val appletContext: AppletContext
    val codeBase: URL?
    val documentBase: URL?
    fun getAudioClip(url: URL): AudioClip
    fun getAudioClip(url: InputStream): AudioClip
    fun getImage(url: URL): Image
    fun getImage(url: InputStream): Image
    fun getApplet(name: String): Applet?
    fun getParameter(name: String): String?
    fun isActive(): Boolean
    fun showStatus(msg: String)
    fun init()
    fun start()
    fun stop()
    fun destroy()

}
